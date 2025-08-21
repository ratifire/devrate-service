package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewFeedbackDto;
import com.ratifire.devrate.dto.InterviewHistoryDto;
import com.ratifire.devrate.dto.InterviewStatsConductedPassedByDateDto;
import com.ratifire.devrate.dto.SkillFeedbackDto;
import com.ratifire.devrate.dto.projection.UserNameProjection;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewHistory;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.enums.TimeSlotStatus;
import com.ratifire.devrate.exception.InterviewHistoryNotFoundException;
import com.ratifire.devrate.mapper.impl.InterviewHistoryMapper;
import com.ratifire.devrate.repository.UserRepository;
import com.ratifire.devrate.repository.interview.InterviewHistoryRepository;
import com.ratifire.devrate.repository.interview.InterviewRequestTimeSlotRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.service.EventService;
import com.ratifire.devrate.service.MasteryService;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing interview summaries.
 */
@Service
@RequiredArgsConstructor
public class InterviewHistoryService {

  private final InterviewService interviewService;
  private final MasteryService masteryService;
  private final InterviewHistoryRepository interviewHistoryRepository;
  private final UserContextProvider userContextProvider;
  private final InterviewHistoryMapper interviewHistoryMapper;
  private final UserRepository userRepository;
  private final EventService eventService;
  private final InterviewRequestTimeSlotRepository timeSlotRepository;

  /**
   * Retrieves an InterviewSummary entity by its identifier.
   *
   * @param id the unique identifier of the InterviewSummary to be retrieved.
   * @return the InterviewSummary associated with the provided id.
   * @throws InterviewHistoryNotFoundException if no InterviewSummary is found for the given id.
   */
  public InterviewHistoryDto findByIdAndUserId(long id) {
    long userId = userContextProvider.getAuthenticatedUserId();

    InterviewHistory interviewHistory =
        interviewHistoryRepository.findByIdAndUserIdAndIsVisibleTrue(id, userId)
            .orElseThrow(() -> new InterviewHistoryNotFoundException(id));
    return interviewHistoryMapper.toDto(interviewHistory);
  }

  /**
   * Retrieves an InterviewHistory entity by its interview ID and user ID.
   *
   * @param interviewId the unique identifier of the interview.
   * @param userId      the unique identifier of the user.
   * @return an Optional containing the InterviewHistory.
   */
  public Optional<InterviewHistory> findByInterviewIdAndUserId(long interviewId, long userId) {
    return interviewHistoryRepository.findByInterviewIdAndUserId(interviewId, userId);
  }

  /**
   * Retrieves all InterviewHistory entities associated with the authenticated user.
   *
   * @return a list of InterviewHistory entities linked to the current user.
   */
  public Page<InterviewHistoryDto> getAllByUserId(int page, int size) {
    long userId = userContextProvider.getAuthenticatedUserId();
    Pageable pageable = PageRequest.of(page, size, Sort.by("dateTime").descending());
    return interviewHistoryRepository.findAllByUserIdAndIsVisibleTrue(userId, pageable)
        .map(interviewHistoryMapper::toDto);
  }

  /**
   * Deletes an InterviewHistory entity by its identifier.
   *
   * @param id the unique identifier of the InterviewHistory to be deleted.
   * @throws InterviewHistoryNotFoundException if no InterviewHistory is found for the given id.
   */
  @Transactional
  public void delete(long id) {
    InterviewHistory interviewHistory = interviewHistoryRepository.findById(id)
        .orElseThrow(() -> new InterviewHistoryNotFoundException(id));

    List<User> users = userRepository.findAllByInterviewHistoriesContaining(interviewHistory);
    users.forEach(user -> user.getInterviewHistories().remove(interviewHistory));

    userRepository.saveAll(users);
    interviewHistoryRepository.delete(interviewHistory);
  }

  /**
   * Updates the existing interview history records based on the provided feedback.
   *
   * @param feedbackDto             the DTO containing feedback data for the interview
   * @param currentInterviewHistory the current interview history to be updated
   */
  public Map<InterviewRequestRole, InterviewHistory> updateExisting(
      InterviewFeedbackDto feedbackDto,
      InterviewHistory currentInterviewHistory) {
    long currentInterviewId = feedbackDto.getInterviewId();
    Interview oppositeInterview = interviewService.findOppositeInterview(currentInterviewId)
        .orElseThrow(() -> new IllegalStateException(
            "Opposite interview not found for interview ID: " + currentInterviewId));

    long oppositeInterviewId = oppositeInterview.getId();
    InterviewHistory oppositeInterviewHistory =
        findByInterviewIdAndUserId(oppositeInterviewId, oppositeInterview.getUserId())
            .orElseThrow(() -> new IllegalStateException(
                "Opposite interview history not found for interview ID: " + oppositeInterviewId));

    oppositeInterviewHistory.setSoftSkills(
        convertEvaluatedSkills(feedbackDto.getSkills(), SkillType.SOFT_SKILL));
    oppositeInterviewHistory.setHardSkills(
        convertEvaluatedSkills(feedbackDto.getSkills(), SkillType.HARD_SKILL));
    oppositeInterviewHistory.setFeedback(feedbackDto.getFeedback());
    oppositeInterviewHistory.setVideoUrl(oppositeInterview.getVideoUrl());

    currentInterviewHistory.setIsVisible(true);

    List<InterviewHistory> interviewHistories = List.of(currentInterviewHistory,
        oppositeInterviewHistory);
    interviewHistoryRepository.saveAll(interviewHistories);
    timeSlotRepository.markTimeSlotsAsCompleted(currentInterviewId,
        currentInterviewHistory.getId(), TimeSlotStatus.COMPLETED);
    interviewService.deleteByIds(List.of(currentInterviewId, oppositeInterviewId));
    eventService.delete(oppositeInterview.getEventId());

    return interviewHistories.stream()
        .collect(Collectors.toMap(InterviewHistory::getRole, Function.identity()));
  }

  /**
   * Creates new interview history records based on the provided feedback.
   *
   * @param feedbackDto the DTO containing feedback data for the interview
   */
  public void create(InterviewFeedbackDto feedbackDto) {
    long currentInterviewId = feedbackDto.getInterviewId();

    Interview currentInterview = interviewService.findByIdAndUserId(currentInterviewId)
        .orElseThrow(() -> new IllegalStateException(
            "Interview not found for ID: " + currentInterviewId));

    Interview oppositeInterview = interviewService.findOppositeInterview(currentInterviewId)
        .orElseThrow(() -> new IllegalStateException(
            "Opposite interview not found for interview ID: " + currentInterviewId));

    Mastery currentMastery = masteryService.getMasteryById(currentInterview.getMasteryId());
    Mastery oppositeMastery = masteryService.getMasteryById(oppositeInterview.getMasteryId());

    long currentUserId = currentInterview.getUserId();
    long oppositeUserId = oppositeInterview.getUserId();

    UserNameProjection currentUserName = userRepository.findUserNameByUserId(currentUserId);
    UserNameProjection oppositeUserName = userRepository.findUserNameByUserId(oppositeUserId);

    InterviewHistory currentInterviewHistory = buildInterviewHistory(
        currentInterview,
        currentMastery,
        oppositeMastery,
        oppositeUserId,
        oppositeUserName.getFirstName(),
        oppositeUserName.getLastName(),
        null,
        null,
        null,
        currentInterview.getVideoUrl(),
        true
    );

    InterviewHistory oppositeInterviewHistory = buildInterviewHistory(
        oppositeInterview,
        oppositeMastery,
        currentMastery,
        currentUserId,
        currentUserName.getFirstName(),
        currentUserName.getLastName(),
        convertEvaluatedSkills(feedbackDto.getSkills(), SkillType.SOFT_SKILL),
        convertEvaluatedSkills(feedbackDto.getSkills(), SkillType.HARD_SKILL),
        feedbackDto.getFeedback(),
        null,
        false
    );

    List<InterviewHistory> savedInterviewHistories = interviewHistoryRepository.saveAll(
        List.of(currentInterviewHistory, oppositeInterviewHistory));

    Long currentInterviewHistoryId = savedInterviewHistories.stream()
        .filter(i -> i.getInterviewId() == currentInterviewId)
        .map(InterviewHistory::getId).findFirst().orElse(null);
    timeSlotRepository.markTimeSlotsAsCompleted(currentInterviewId,
        currentInterviewHistoryId, TimeSlotStatus.COMPLETED);

    currentInterview.setVisible(false);
    interviewService.save(currentInterview);
  }

  private InterviewHistory buildInterviewHistory(
      Interview interview,
      Mastery mastery,
      Mastery attendeeMastery,
      long attendeeId,
      String attendeeFirstName,
      String attendeeLastName,
      Map<String, BigDecimal> softSkills,
      Map<String, BigDecimal> hardSkills,
      String feedback,
      String videoUrl,
      boolean isVisible) {
    return InterviewHistory.builder()
        .dateTime(interview.getStartTime())
        .duration(60L)
        .userId(interview.getUserId())
        .softSkills(softSkills)
        .hardSkills(hardSkills)
        .masteryId(mastery.getId())
        .specialization(mastery.getSpecialization().getName())
        .masteryLevel(mastery.getLevel())
        .role(interview.getRole())
        .consentStatus(interview.getConsentStatus())
        .attendeeId(attendeeId)
        .attendeeFirstName(attendeeFirstName)
        .attendeeLastName(attendeeLastName)
        .attendeeMasteryLevel(attendeeMastery.getLevel())
        .attendeeSpecialization(attendeeMastery.getSpecialization().getName())
        .feedback(feedback)
        .isVisible(isVisible)
        .interviewId(interview.getId())
        .videoUrl(videoUrl)
        .build();
  }

  private Map<String, BigDecimal> convertEvaluatedSkills(List<SkillFeedbackDto> skills,
      SkillType type) {
    return skills.stream()
        .filter(skill -> skill.getType() == type)
        .collect(Collectors.toMap(SkillFeedbackDto::getName, SkillFeedbackDto::getMark));
  }

  /**
   * Counts the number of conducted and passed interviews for the specified user within a given date
   * range.
   *
   * @param from the start date of the date range (inclusive)
   * @param to   the end date of the date range (inclusive)
   * @return a list with the count of conducted and passed interviews per date.
   */
  public List<InterviewStatsConductedPassedByDateDto> getInterviewsConductedPassed(
      ZonedDateTime from, ZonedDateTime to) {
    long userId = userContextProvider.getAuthenticatedUserId();

    List<InterviewHistory> interviewHistories = interviewHistoryRepository
        .findByUserIdAndDateTimeBetween(userId, from, to);

    Map<ZonedDateTime, InterviewStatsConductedPassedByDateDto> interviewStats = new HashMap<>();

    for (InterviewHistory interview : interviewHistories) {
      ZonedDateTime date = interview.getDateTime();
      interviewStats.putIfAbsent(date, new InterviewStatsConductedPassedByDateDto(date, 0, 0));

      if (InterviewRequestRole.CANDIDATE == interview.getRole()) {
        interviewStats.get(date).setPassed(interviewStats.get(date).getPassed() + 1);
      }

      if (InterviewRequestRole.INTERVIEWER == interview.getRole()) {
        interviewStats.get(date).setConducted(interviewStats.get(date).getConducted() + 1);
      }
    }

    return interviewStats.values().stream()
        .sorted(Comparator.comparing(InterviewStatsConductedPassedByDateDto::getDate)).toList();
  }
}