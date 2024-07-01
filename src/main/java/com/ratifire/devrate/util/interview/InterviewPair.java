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
public class InterviewPair<C, I> {

  private C candidate;
  private I interviewer;

  /**
   * Creates an InterviewPair based on the roles of the requests.
   *
   * @param incomingRequest the incoming interview request
   * @param matchedRequest the matched interview request
   * @return an InterviewPair with roles assigned based on the requests
   */
  public static InterviewPair<InterviewRequest, InterviewRequest> getPair(
      InterviewRequest incomingRequest, InterviewRequest matchedRequest) {
    return (incomingRequest.getRole() == CANDIDATE)
        ? InterviewPair.<InterviewRequest, InterviewRequest>builder()
            .candidate(incomingRequest)
            .interviewer(matchedRequest)
            .build()
        : InterviewPair.<InterviewRequest, InterviewRequest>builder()
            .candidate(matchedRequest)
            .interviewer(incomingRequest)
            .build();
  }
}