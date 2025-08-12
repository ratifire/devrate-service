package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.dto.InterviewRequestViewDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.entity.interview.InterviewRequestTimeSlot;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.enums.TimeSlotStatus;
import com.ratifire.devrate.exception.InterviewRequestDoesntExistException;
import com.ratifire.devrate.exception.InterviewRequestInvalidSkillCountException;
import com.ratifire.devrate.exception.InterviewRequestNotFoundException;
import com.ratifire.devrate.exception.InvalidInterviewRequestException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.impl.InterviewRequestMapper;
import com.ratifire.devrate.mapper.impl.InterviewRequestTimeSlotMapper;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import com.ratifire.devrate.repository.interview.InterviewRequestTimeSlotRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Service for handling Interview Requests.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewRequestService {

  private static final long LOW_SKILL_LIMIT = 3;
  private static final long HIGH_SKILL_LIMIT = 20;

  private final InterviewRequestRepository repository;
  private final InterviewRequestMapper mapper;
  private final MatcherServiceQueueSender matcherServiceQueueSender;
  private final UserContextProvider userContextProvider;
  private final InterviewRequestTimeSlotRepository timeSlotRepository;
  private final InterviewRequestTimeSlotMapper timeSlotMapper;

  public Optional<Long> findMasteryId(long id) {
    return repository.findMasteryIdById(id);
  }

  public List<InterviewRequest> findByIds(List<Long> ids) {
    return repository.findAllById(ids);
  }

  /**
   * Retrieves the interview ID associated with the given time slot ID.
   *
   * @param timeSlotId the ID of the InterviewRequestTimeSlot
   * @return the ID of the associated interview, or {@code null} if none
   * @throws ResourceNotFoundException if the time slot is not found
   */
  public Long getInterviewIdByTimeSlotId(long timeSlotId) {
    return timeSlotRepository.findById(timeSlotId).orElseThrow(
            () -> new ResourceNotFoundException("Time slot not found with ID: " + timeSlotId))
        .getInterviewId();
  }

  /**
   * Retrieves the interview history ID associated with the given time slot ID.
   *
   * @param timeSlotId the ID of the InterviewRequestTimeSlot
   * @return the ID of the associated interview history, or {@code null} if none
   * @throws ResourceNotFoundException if the time slot is not found
   */
  public Long getInterviewHistoryIdByTimeSlotId(long timeSlotId) {
    return timeSlotRepository.findById(timeSlotId).orElseThrow(
            () -> new ResourceNotFoundException("Time slot not found with ID: " + timeSlotId))
        .getInterviewHistoryId();
  }

  /**
   * Retrieves all interview requests for the authenticated user.
   *
   * @return a list of {@link InterviewRequestViewDto} containing the details.
   */
  public List<InterviewRequestViewDto> getAll() {
    long userId = userContextProvider.getAuthenticatedUserId();
    List<InterviewRequest> interviewRequests = repository.findAllByUser_Id(userId);
    updateExpiredTimeSlots(interviewRequests);
    return constructInterviewRequestViewDto(interviewRequests);
  }

  /**
   * Retrieves a list of interview requests by mastery ID for the authenticated user.
   *
   * @param masteryId the ID of the mastery to fetch interview requests for.
   * @return a list of {@link InterviewRequestViewDto} containing the details.
   */
  public List<InterviewRequestViewDto> getByMasteryId(long masteryId) {
    long userId = userContextProvider.getAuthenticatedUserId();
    List<InterviewRequest> interviewRequests = repository.findAllByMastery_IdAndUser_Id(masteryId,
        userId);
    updateExpiredTimeSlots(interviewRequests);
    return constructInterviewRequestViewDto(interviewRequests);
  }

  private void updateExpiredTimeSlots(List<InterviewRequest> interviewRequests) {
    if (CollectionUtils.isEmpty(interviewRequests)) {
      return;
    }

    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    boolean hasExpiredSlots = false;
    for (InterviewRequest request : interviewRequests) {
      for (InterviewRequestTimeSlot slot : request.getTimeSlots()) {
        if (TimeSlotStatus.PENDING == slot.getStatus() && slot.getDateTime().isBefore(now)) {
          slot.setStatus(TimeSlotStatus.EXPIRED);
          hasExpiredSlots = true;
        }
      }
    }

    if (hasExpiredSlots) {
      repository.saveAll(interviewRequests);
    }
  }

  private List<InterviewRequestViewDto> constructInterviewRequestViewDto(
      List<InterviewRequest> interviewRequests) {
    List<Skill> skills = interviewRequests.stream()
        .findFirst()
        .map(ir -> ir.getMastery().getSkills())
        .orElse(List.of());

    long hardSkillCount = countSkillsByType(skills, SkillType.HARD_SKILL);
    long softSkillCount = countSkillsByType(skills, SkillType.SOFT_SKILL);

    return interviewRequests.stream()
        .map(request -> mapToInterviewRequestViewDto(request, hardSkillCount, softSkillCount))
        .toList();
  }

  /**
   * Converts an InterviewRequest entity to an InterviewRequestViewDto.
   *
   * @param request the InterviewRequest entity to convert.
   * @return the converted {@link InterviewRequestViewDto}.
   */
  private InterviewRequestViewDto mapToInterviewRequestViewDto(InterviewRequest request,
      long hardSkillCount, long softSkillCount) {
    return InterviewRequestViewDto.builder()
        .id(request.getId())
        .role(request.getRole())
        .desiredInterview(request.getDesiredInterview())
        .matchedInterview(request.getMatchedInterview())
        .comment(request.getComment())
        .languageCode(request.getLanguageCode())
        .hardSkillCount(hardSkillCount)
        .softSkillCount(softSkillCount)
        .timeSlots(timeSlotMapper.toDto(request.getTimeSlots()))
        .build();
  }

  /**
   * Creates an interview request for the specified user and send it to matcher-service.
   *
   * @param requestDto the DTO containing the interview request details
   */
  @Transactional
  public void create(InterviewRequestDto requestDto) {
    validateTimeSlots(requestDto.getTimeSlots(), requestDto.getDesiredInterview());

    long userId = userContextProvider.getAuthenticatedUserId();

    InterviewRequest interviewRequest = mapper.toEntity(requestDto);
    interviewRequest.setUser(User.builder().id(userId).build());
    interviewRequest.setBlackList(new HashSet<>());

    validateSkills(interviewRequest);

    List<InterviewRequestTimeSlot> interviewRequestTimeSlots =
        createTimeSlots(requestDto.getTimeSlots());

    interviewRequestTimeSlots.forEach(slot -> slot.setInterviewRequest(interviewRequest));

    interviewRequest.setTimeSlots(interviewRequestTimeSlots);

    repository.save(interviewRequest);

    matcherServiceQueueSender.create(interviewRequest);
  }

  private List<InterviewRequestTimeSlot> createTimeSlots(List<ZonedDateTime> timesSlots) {
    return timesSlots.stream()
        .map(timesSlot -> InterviewRequestTimeSlot.builder()
            .dateTime(timesSlot)
            .status(TimeSlotStatus.PENDING)
            .build())
        .toList();
  }

  /**
   * Updates the interview request for the specified user based on the provided request DTO.
   *
   * @param requestDto the interview request details
   */
  @Transactional
  public void update(long id, InterviewRequestDto requestDto) {
    long userId = userContextProvider.getAuthenticatedUserId();

    InterviewRequest interviewRequest = repository.findByIdAndUser_Id(id, userId)
        .orElseThrow(() -> new InterviewRequestDoesntExistException(id, userId));

    validateSkills(interviewRequest);

    // Check that desiredInterview was changed
    if (requestDto.getDesiredInterview() != interviewRequest.getDesiredInterview()) {
      List<ZonedDateTime> pendingDateTime = interviewRequest.getTimeSlots().stream()
          .filter(dateTime -> TimeSlotStatus.PENDING == dateTime.getStatus())
          .map(InterviewRequestTimeSlot::getDateTime)
          .toList();

      validateTimeSlots(pendingDateTime, requestDto.getDesiredInterview());
    }

    mapper.updateEntity(requestDto, interviewRequest);
    repository.save(interviewRequest);

    matcherServiceQueueSender.update(interviewRequest);
  }

  private void validateSkills(InterviewRequest interviewRequest) {
    List<Skill> skills = interviewRequest.getMastery().getSkills();

    long hardSkillCount = countSkillsByType(skills, SkillType.HARD_SKILL);
    long softSkillCount = countSkillsByType(skills, SkillType.SOFT_SKILL);

    if (isInvalidSkillCount(hardSkillCount) || isInvalidSkillCount(softSkillCount)) {
      throw new InterviewRequestInvalidSkillCountException(interviewRequest.getId(), hardSkillCount,
          softSkillCount);
    }
  }

  private long countSkillsByType(List<Skill> skills, SkillType type) {
    return skills.stream().filter(s -> s.getType() == type).count();
  }

  private boolean isInvalidSkillCount(long count) {
    return count < LOW_SKILL_LIMIT || count > HIGH_SKILL_LIMIT;
  }

  private void validateTimeSlots(List<ZonedDateTime> timeSlots, int desiredInterview) {
    if (CollectionUtils.isEmpty(timeSlots)) {
      throw new InvalidInterviewRequestException("Time slots is a required parameters.");
    }
    if (timeSlots.size() < desiredInterview) {
      throw new InvalidInterviewRequestException("Count of pending dates \"" + timeSlots.size()
          + "\" must be greater than or equal to the desired number \""
          + desiredInterview + "\" of interviews.");
    }

  }

  /**
   * Updates the status and interview ID for time slots corresponding to the provided interviewer
   * and candidate interview requests on the specified scheduled date.
   *
   * @param interviewerRequestId the ID of the interviewer's {@link InterviewRequest}
   * @param candidateRequestId   the ID of the candidate's {@link InterviewRequest}
   * @param scheduledDate        the scheduled interview date and time
   * @param interviews           the list of {@link Interview} entities;
   */
  @Transactional
  public void updateTimeSlots(long interviewerRequestId, long candidateRequestId,
      ZonedDateTime scheduledDate, List<Interview> interviews) {

    // TODO: bad map key, better use Long as key (interviewId)
    Map<InterviewRequestRole, Long> roleToId = interviews.stream()
        .collect(Collectors.toMap(Interview::getRole, Interview::getId));

    List<InterviewRequestTimeSlot> slotsToSave = Stream.of(
            updateTimeSlot(interviewerRequestId, scheduledDate,
                roleToId.get(InterviewRequestRole.INTERVIEWER)),
            updateTimeSlot(candidateRequestId, scheduledDate,
                roleToId.get(InterviewRequestRole.CANDIDATE))).filter(Objects::nonNull)
        .collect(Collectors.toList());

    timeSlotRepository.saveAll(slotsToSave);
  }

  private InterviewRequestTimeSlot updateTimeSlot(long requestId, ZonedDateTime scheduledDate,
      Long interviewId) {
    return timeSlotRepository
        .findInterviewRequestTimeSlotsByInterviewRequestIdAndDateTime(requestId, scheduledDate)
        .map(slot -> {
          slot.setStatus(TimeSlotStatus.BOOKED);
          slot.setInterviewId(interviewId);
          return slot;
        })
        .orElseGet(() -> {
          log.warn("Time slot not found for requestId: {} and date: {}", requestId, scheduledDate);
          return null;
        });
  }

  /**
   * Increments the matched interview count for each InterviewRequest in the given list.
   *
   * @param requests the list of InterviewRequest entities to be updated
   */
  public void incrementMatchedInterviewCount(List<InterviewRequest> requests) {
    if (CollectionUtils.isEmpty(requests)) {
      throw new IllegalArgumentException("Request list cannot be null or empty");
    }
    requests.forEach(request -> request.setMatchedInterview(request.getMatchedInterview() + 1));
    repository.saveAll(requests);
  }

  /**
   * Marks rejected interview time slots as pending.
   *
   * @param interviews the list of rejected interviews to process
   */
  public void markRejectedInterviewTimeSlotsAsPending(List<Interview> interviews) {
    if (CollectionUtils.isEmpty(interviews)) {
      log.warn("No interviews provided for rejection handling");
      return;
    }

    Map<Long, InterviewRequestRole> roleByMastery = interviews.stream()
        .collect(Collectors.toMap(Interview::getMasteryId, Interview::getRole, (r1, r2) -> r1));

    List<Long> masteryIds = new ArrayList<>(roleByMastery.keySet());
    List<InterviewRequestRole> roles = new ArrayList<>(roleByMastery.values());

    // TODO: need improvements, List<InterviewRequest> from the repository should return interview
    //  request that already have paired (masteryId + role) like a key. And after that we don't make
    //  code below.
    // Perform a single query to fetch all requests (may return some incorrect combinations)
    List<InterviewRequest> allRequests = repository.findByMastery_IdInAndRoleIn(masteryIds, roles);

    // Filter out only the correct masteryId + role pairs
    List<InterviewRequest> interviewRequestsToUpdate = allRequests.stream()
        .filter(req -> roleByMastery.containsKey(req.getMastery().getId())
            && roleByMastery.get(req.getMastery().getId()).equals(req.getRole()))
        .toList();

    if (interviewRequestsToUpdate.isEmpty()) {
      log.warn("Rejected interview doesn't have available interview request");
      return;
    }

    ZonedDateTime interviewStartTime = interviews.getFirst().getStartTime();
    timeSlotRepository.markTimeSlotsAsPending(interviewRequestsToUpdate, interviewStartTime,
        TimeSlotStatus.PENDING);

    // Fetch the latest updated InterviewRequests from DB
    List<Long> requestIds = interviewRequestsToUpdate.stream()
        .map(InterviewRequest::getId)
        .toList();
    List<InterviewRequest> updatedRequests = repository.findAllById(requestIds);

    updatedRequests.forEach(matcherServiceQueueSender::update);
  }

  /**
   * Add time slots to the specific interview request.
   *
   * @param id        the interview request ID
   * @param dateTimes list of the time slots that should be added
   */
  public void addTimeSlots(long id, List<ZonedDateTime> dateTimes) {
    InterviewRequest interviewRequest = repository.findById(id)
        .orElseThrow(() -> new InterviewRequestNotFoundException(id));

    validateSkills(interviewRequest);

    List<InterviewRequestTimeSlot> timeSlotsToAdd = dateTimes.stream()
        .map(dateTime -> InterviewRequestTimeSlot.builder()
            .interviewRequest(interviewRequest)
            .dateTime(dateTime)
            .status(TimeSlotStatus.PENDING)
            .build())
        .toList();

    if (!CollectionUtils.isEmpty(timeSlotsToAdd)) {
      interviewRequest.getTimeSlots().addAll(timeSlotsToAdd);
      repository.save(interviewRequest);

      matcherServiceQueueSender.update(interviewRequest);
    }
  }

  /**
   * Delete time slots to the specific interview request.
   *
   * @param id        the interview request ID
   * @param dateTimes list of the time slots that should be deleted
   */
  public void deleteTimeSlots(long id, List<ZonedDateTime> dateTimes) {
    InterviewRequest interviewRequest = repository.findById(id)
        .orElseThrow(() -> new InterviewRequestNotFoundException(id));

    List<InterviewRequestTimeSlot> requestTimeSlots = interviewRequest.getTimeSlots();

    List<InterviewRequestTimeSlot> timeSlotsToDelete = requestTimeSlots.stream()
        .filter(timeSlot -> dateTimes.contains(timeSlot.getDateTime()))
        .toList();

    if (!CollectionUtils.isEmpty(timeSlotsToDelete)) {
      requestTimeSlots.removeAll(timeSlotsToDelete);
      repository.save(interviewRequest);

      matcherServiceQueueSender.update(interviewRequest);
    }
  }

  /**
   * Deletes an interview request by its ID and user ID.
   *
   * @param id the ID of the interview request to be deleted
   */
  @Transactional
  public void delete(long id) {
    long userId = userContextProvider.getAuthenticatedUserId();
    repository.deleteByIdAndUser_Id(id, userId);

    matcherServiceQueueSender.delete(id);
  }
}
