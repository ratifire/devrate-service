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
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing interview feedback details.
 */
@Service
@RequiredArgsConstructor
public class InterviewFeedbackDetailService {

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
  public Map<String, Long> saveInterviewFeedbackDetail(Interview interview,
      long interviewSummaryId) {
    ZonedDateTime interviewStartTime = interview.getStartTime();
    return Map.of(
        "candidateFeedbackId", interviewFeedbackDetailRepository.save(
            createInterviewFeedbackDetail(interview.getCandidateRequest().getUser(),
                interviewSummaryId, interview.getInterviewerRequest(), interviewStartTime)).getId(),
        "interviewerFeedbackId", interviewFeedbackDetailRepository.save(
            createInterviewFeedbackDetail(interview.getInterviewerRequest().getUser(),
                interviewSummaryId, interview.getCandidateRequest(), interviewStartTime)).getId()
    );
  }

  /**
   * Creates an InterviewFeedbackDetail object for either the interviewer or candidate.
   *
   * @param user               the user entity containing information about the participant
   * @param interviewSummaryId the ID of the interview summary to associate the feedback with
   * @param request            the interview request containing information about the role and
   *                           skills of the participant
   * @param startTime          the start time of the interview
   * @return the created InterviewFeedbackDetail entity
   */
  private InterviewFeedbackDetail createInterviewFeedbackDetail(User user, long interviewSummaryId,
      InterviewRequest request, ZonedDateTime startTime) {
    List<Long> skillsIds = request.getRole() == InterviewRequestRole.INTERVIEWER
        ? request.getMastery().getSkills().stream()
        .filter(s -> s.getType() == SkillType.SOFT_SKILL)
        .map(Skill::getId)
        .toList()
        : request.getMastery().getSkills().stream()
            .map(Skill::getId)
            .toList();

    return InterviewFeedbackDetail.builder()
        .user(user)
        .participantRole(request.getRole())
        .startTime(startTime)
        .interviewSummaryId(interviewSummaryId)
        .evaluatedMasteryId(request.getMastery().getId())
        .skillsIds(skillsIds)
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
}