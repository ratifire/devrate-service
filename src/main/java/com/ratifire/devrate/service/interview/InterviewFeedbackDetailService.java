package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewFeedbackDetailDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewFeedbackDetail;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.exception.InterviewFeedbackDetailNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.interview.InterviewFeedbackDetailRepository;
import com.ratifire.devrate.service.specialization.SkillService;
import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service class for managing interview feedback details.
 */
@Service
@RequiredArgsConstructor
public class InterviewFeedbackDetailService {

  private static final long CLEANUP_INTERVAL = 2592000000L; // 30 days in milliseconds
  private static final int RECORD_RETENTION_PERIOD_IN_MONTHS = 1;
  private final InterviewFeedbackDetailRepository interviewFeedbackDetailRepository;
  private final DataMapper<InterviewFeedbackDetailDto, InterviewFeedbackDetail>
      interviewFeedbackDetailMapper;
  private final SkillService skillService;


  /**
   * Retrieves feedback details by the given feedback detail ID.
   *
   * @param id the ID of the feedback detail
   * @return the FeedbackDetailDto containing the feedback detail and associated skills
   * @throws InterviewFeedbackDetailNotFoundException if the feedback detail is not found by the
   *                                                  given ID
   */
  public InterviewFeedbackDetailDto getInterviewFeedbackDetail(long id) {
    InterviewFeedbackDetail feedbackDetail = findDetailById(id);
    return interviewFeedbackDetailMapper.toDto(feedbackDetail,
        skillService.findAllById(feedbackDetail.getSkillsIds()));
  }

  /**
   * Saves interview feedback details for both the interviewer and candidate of an interview.
   *
   * @param interview          the interview entity containing information about the interview
   * @param interviewSummaryId the ID of the interview summary to associate the feedback details
   *                           with
   * @return a map containing the IDs of the saved feedback details for the interviewer and
   *     candidate
   */
  public Map<InterviewRequestRole, Long> saveInterviewFeedbackDetail(Interview interview,
      long interviewSummaryId) {
    ZonedDateTime startTime = interview.getStartTime();
    User candidate = interview.getCandidateRequest().getUser();
    User interviewer = interview.getInterviewerRequest().getUser();
    InterviewFeedbackDetail candidateFeedbackDetail = createInterviewFeedbackDetail(
        candidate.getId(), interviewer, interviewSummaryId, interview.getInterviewerRequest(),
        startTime);
    InterviewFeedbackDetail interviewerFeedbackDetail = createInterviewFeedbackDetail(
        interviewer.getId(), candidate, interviewSummaryId, interview.getCandidateRequest(),
        startTime);

    return Map.of(
        InterviewRequestRole.CANDIDATE,
        interviewFeedbackDetailRepository.save(candidateFeedbackDetail).getId(),
        InterviewRequestRole.INTERVIEWER,
        interviewFeedbackDetailRepository.save(interviewerFeedbackDetail).getId()
    );
  }

  /**
   * Creates an InterviewFeedbackDetail object for either the interviewer or candidate.
   *
   * @param ownerId            the host feedback detail user id
   * @param participant        the user entity containing information about the participant
   * @param interviewSummaryId the ID of the interview summary to associate the feedback with
   * @param request            the interview request containing information about the role and
   *                           skills of the participant
   * @param startTime          the start time of the interview
   * @return the created InterviewFeedbackDetail entity
   */
  private InterviewFeedbackDetail createInterviewFeedbackDetail(long ownerId,
      User participant, long interviewSummaryId, InterviewRequest request,
      ZonedDateTime startTime) {
    List<Long> skillsIds = request.getRole() == InterviewRequestRole.INTERVIEWER
        ? request.getMastery().getSkills().stream()
        .filter(s -> s.getType() == SkillType.SOFT_SKILL)
        .map(Skill::getId)
        .toList()
        : request.getMastery().getSkills().stream()
            .map(Skill::getId)
            .toList();

    return InterviewFeedbackDetail.builder()
        .participant(participant)
        .participantRole(request.getRole())
        .startTime(startTime)
        .interviewSummaryId(interviewSummaryId)
        .evaluatedMasteryId(request.getMastery().getId())
        .skillsIds(skillsIds)
        .ownerId(ownerId)
        .build();
  }

  /**
   * Finds the interview feedback detail by its ID.
   *
   * @param id the ID of the interview feedback detail to find
   * @return the InterviewFeedbackDetail entity if found
   * @throws InterviewFeedbackDetailNotFoundException if no feedback detail is found with the given
   *                                                  ID
   */
  public InterviewFeedbackDetail findDetailById(Long id) {
    return interviewFeedbackDetailRepository.findById(id)
        .orElseThrow(() -> new InterviewFeedbackDetailNotFoundException(id));
  }

  /**
   * Deletes the interview feedback detail by its ID.
   *
   * @param id the ID of the interview feedback detail to delete
   */
  public void deleteById(long id) {
    interviewFeedbackDetailRepository.deleteById(id);
  }

  /**
   * Scheduled task to delete expired interview feedback details from the database.
   *
   * <p>This method runs periodically based on the {@code CLEANUP_INTERVAL} to remove records
   * from the {@code interview_feedback_details} table where the interview start time is older than
   * the specified {@code RECORD_RETENTION_PERIOD_IN_MONTHS}.
   */
  @Transactional
  @Scheduled(fixedRate = CLEANUP_INTERVAL, initialDelay = 86400000) // initialDelay parameter is
  // only for development phase, before release it must be removed
  public void deleteExpiredInterviewFeedbackDetailsTask() {
    ZonedDateTime expiredRecordsCutoffDate = ZonedDateTime.now()
        .minusMonths(RECORD_RETENTION_PERIOD_IN_MONTHS);
    interviewFeedbackDetailRepository.deleteExpiredFeedbackDetails(expiredRecordsCutoffDate);
  }
}