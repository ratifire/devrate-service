package com.ratifire.devrate.dto;

import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Transfer Object for a matched interview pair.
 */
@Builder
@Getter
public class MatchedInterviewPairDto {

  private InterviewRequest candidate;
  private InterviewRequest interviewer;
  private static final Logger logger = LoggerFactory.getLogger(MatchedInterviewPairDto.class);

  /**
   * Creates a MatchedInterviewPairDto from a list of matched interview requests.
   *
   * @param matchedRequests the list of matched interview requests
   * @return the created MatchedInterviewPairDto
   */
  public static MatchedInterviewPairDto createPair(List<InterviewRequest> matchedRequests) {
    return MatchedInterviewPairDto.builder()
        .candidate(getCandidate(matchedRequests))
        .interviewer(getInterviewer(matchedRequests))
        .build();
  }

  /**
   * Retrieves the candidate from the list of matched interview requests.
   *
   * @param matchedRequests the list of matched interview requests
   * @return the interview request of the candidate
   */
  private static InterviewRequest getCandidate(List<InterviewRequest> matchedRequests) {
    return matchedRequests.stream()
        .filter(request -> request.getRole() == InterviewRequestRole.CANDIDATE)
        .findFirst()
        .orElseGet(() -> {
          logger.info("No matching candidate request found. Returning default InterviewRequest.");
          return InterviewRequest.builder().build();
        });
  }

  /**
   * Retrieves the interviewer from the list of matched interview requests.
   *
   * @param matchedRequests the list of matched interview requests
   * @return the interview request of the interviewer
   */
  private static InterviewRequest getInterviewer(List<InterviewRequest> matchedRequests) {
    return matchedRequests.stream()
        .filter(request -> request.getRole() == InterviewRequestRole.CANDIDATE)
        .findFirst()
        .orElseGet(() -> {
          logger.info("No matching interview request found. Returning default InterviewRequest.");
          return InterviewRequest.builder().build();
        });
  }
}
