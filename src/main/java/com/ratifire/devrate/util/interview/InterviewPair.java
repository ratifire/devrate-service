package com.ratifire.devrate.util.interview;

import static com.ratifire.devrate.enums.InterviewRequestRole.CANDIDATE;

import com.ratifire.devrate.entity.interview.InterviewRequest;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents a pair of interview participants: a candidate and an interviewer.
 */
@Getter
@Builder
public class InterviewPair {

  private InterviewRequest candidate;
  private InterviewRequest interviewer;

  /**
   * Creates an InterviewPair
   *
   * @param incomingRequest The request to be assigned as the candidate or interviewer.
   * @param matchedRequest  The request to be assigned as the other role.
   * @return An InterviewPair with the appropriate candidate and interviewer.
   */
  public static InterviewPair getPair(InterviewRequest incomingRequest,
      InterviewRequest matchedRequest) {
    return (incomingRequest.getRole() == CANDIDATE)
        ? InterviewPair.builder()
        .candidate(incomingRequest)
        .interviewer(matchedRequest)
        .build()
        : InterviewPair.builder()
            .candidate(matchedRequest)
            .interviewer(incomingRequest)
            .build();
  }
}