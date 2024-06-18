package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for handling Interview operations.
 */
@Service
@RequiredArgsConstructor
public class InterviewService {

  private final InterviewRepository interviewRepository;

  /**
   * Creates a new interview based on the provided candidate and interviewer requests.
   *
   * @param candidate   The request details for the candidate.
   * @param interviewer The request details for the interviewer.
   */
  public void createInterview(InterviewRequest candidate, InterviewRequest interviewer) {
    Interview interview = Interview.builder()
        .candidateRequest(candidate)
        .interviewerRequest(interviewer)
        .startTime(getCommonStartTime(candidate.getDates(), interviewer.getDates()))
        .build();

    interviewRepository.save(interview);
  }

  /**
   * Finds the common start time from the provided candidate and interviewer dates.
   *
   * @param candidateDates   List of dates available for the candidate.
   * @param interviewerDates List of dates available for the interviewer.
   * @return The common start time agreed upon by both parties.
   */
  private ZonedDateTime getCommonStartTime(List<ZonedDateTime> candidateDates,
      List<ZonedDateTime> interviewerDates) {
    return candidateDates.stream()
        .filter(interviewerDates::contains)
        .toList()
        .getFirst();
  }
}
