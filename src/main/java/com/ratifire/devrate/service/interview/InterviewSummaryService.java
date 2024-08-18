package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.InterviewSummary;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.repository.InterviewSummaryRepository;
import com.ratifire.devrate.service.user.UserService;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
  public void saveInterviewSummary(Interview interview, String endTime) {
    User interviewer = interview.getInterviewerRequest().getUser();
    User candidate = interview.getCandidateRequest().getUser();
    ZonedDateTime startTime = interview.getStartTime();

    InterviewSummary interviewSummary = InterviewSummary.builder()
        .date(startTime.toLocalDate())
        .duration(Duration.between(startTime, ZonedDateTime.parse(endTime)).toMinutes())
        .candidateId(candidate.getId())
        .interviewerId(interviewer.getId())
        .build();

    interviewSummaryRepository.save(interviewSummary);

    userService.addInterviewSummaryToUsers(List.of(interviewer, candidate), interviewSummary);
  }
}