package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.MatchedInterviewPairDto;
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
   * Creates an interview based on the matched pair of candidate and interviewer.
   *
   * @param matchedPair the matched pair of candidate and interviewer
   */
  public void createInterview(MatchedInterviewPairDto matchedPair) {
    InterviewRequest candidate = matchedPair.getCandidate();
    InterviewRequest interviewer = matchedPair.getInterviewer();

    Interview interview = Interview.builder()
        .candidateRequest(candidate)
        .interviewerRequest(interviewer)
        .startTime(
            getMatchedStartTime(candidate.getAvailableDates(), interviewer.getAvailableDates()))
        .build();

    interviewRepository.save(interview);
  }

  /**
   * Finds the matched start time from the provided candidate and interviewer dates.
   *
   * @param candidateDates   List of dates available for the candidate.
   * @param interviewerDates List of dates available for the interviewer.
   * @return The matched start time agreed upon by both parties.
   */
  private ZonedDateTime getMatchedStartTime(List<ZonedDateTime> candidateDates,
      List<ZonedDateTime> interviewerDates) {
    return candidateDates.stream()
        .filter(interviewerDates::contains)
        .toList()
        .getFirst();
  }
}
