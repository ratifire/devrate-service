package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.util.parser.DataParser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterviewCompletionService {

  private final InterviewService interviewService;
  private final InterviewSummaryService interviewSummaryService;
  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(
      InterviewCompletionService.class);


  /**
   * Handles the completion process of an interview by performing necessary operations after a
   * meeting has ended.
   *
   * @param meetingId The ID of the meeting that has ended. This ID is expected to be a string
   *                  representation of a long value.
   * @param endTime   The time when the meeting ended, in a string format. This is used to update
   *                  the interview summary.
   * @throws IllegalArgumentException If the meeting ID is not a valid long or if the interview is
   *                                  not found.
   */
  @Transactional
  public ResponseEntity<String> completeInterview(String meetingId, String endTime) {
    try {
      long meetingIdLong = DataParser.parseToLong(meetingId);
      Interview interview = interviewService.getInterviewByMeetingId(meetingIdLong);
      interviewSummaryService.saveInterviewSummary(interview, endTime);
      return new ResponseEntity<>("The interview completion process was successful.",
          HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      logger.error(
          "The interview completion process failed for meeting ID: " + meetingId + ". Reason: "
              + e.getMessage(), e);
      return new ResponseEntity<>(
          "The interview completion process failed. Invalid meeting details provided.",
          HttpStatus.BAD_REQUEST);
    }
  }
}