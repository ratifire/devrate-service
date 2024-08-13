package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.InterviewSummary;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.repository.InterviewSummaryRepository;
import com.ratifire.devrate.service.user.UserService;
import com.ratifire.devrate.util.interview.DateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing interview summaries.
 */
@Service
@RequiredArgsConstructor
public class InterviewSummaryService {

  private final InterviewSummaryRepository interviewSummaryRepository;
  private final UserService userService;


  /**
   * Saves an interview summary based on the provided interview and end time.
   *
   * @param interview the Interview object used to retrieve details for creating the summary
   * @param endTime   the end time of the interview, used to calculate the duration
   */
  @Transactional
  public void saveInterviewSummary(Interview interview, String endTime) {
    User interviewer = interview.getInterviewerRequest().getUser();
    User candidate = interview.getCandidateRequest().getUser();
    ZonedDateTime startTime = interview.getStartTime();

    InterviewSummary interviewSummary = createInterviewSummary(startTime, endTime,
        interviewer.getId(), candidate.getId());

    interviewSummaryRepository.save(interviewSummary);

    userService.addInterviewSummaryToUsers(List.of(interviewer, candidate), interviewSummary);
  }

  /**
   * Creates an InterviewSummary for the given interview data.
   *
   * @param startTime     the start time of the interview
   * @param endTime       the end time of the interview, used to calculate the duration
   * @param interviewerId the ID of the interviewer
   * @param candidateId   the ID of the candidate
   * @return the created InterviewSummary object
   */
  private InterviewSummary createInterviewSummary(ZonedDateTime startTime, String endTime,
      long interviewerId, long candidateId) {
    LocalDate date = startTime.toLocalDate();
    long duration = DateTimeUtils.calculateDurationInterviewInMinutes(startTime, endTime);

    return InterviewSummary.builder()
        .date(date)
        .duration(duration)
        .candidateId(candidateId)
        .interviewerId(interviewerId)
        .build();
  }
}