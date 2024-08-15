package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.util.parser.DataParser;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest.Payload.Meeting;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for handling the completion process of an interview.
 */
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
   * @param meeting The meeting object containing details of the meeting that has ended. This object
   *                includes the meeting ID as a string and the end time of the meeting.
   * @return ResponseEntity with a success message if the interview completion process is
   *     successful, or an error message if the process fails.
   * @throws IllegalArgumentException If the meeting ID is not a valid long or if the interview is
   *                                  not found.
   */
  @Transactional
  public ResponseEntity<String> completeInterviewProcess(Meeting meeting) {
    if (meeting == null) {
      return new ResponseEntity<>("Error: The payload or meeting details are missing.",
          HttpStatus.BAD_REQUEST);
    }

    try {
      long meetingIdLong = DataParser.parseToLong(meeting.getId());
      Interview interview = interviewService.getInterviewByMeetingId(meetingIdLong);
      interviewSummaryService.saveInterviewSummary(interview, meeting.getEndTime());
      //TODO: add the other needed logic for completing the interview process
      return new ResponseEntity<>("The interview completion process was successful.",
          HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      logger.error(
          "The interview completion process failed for meeting ID: " + meeting.getId()
              + ". Reason: " + e.getMessage(), e);
      return new ResponseEntity<>(
          "The interview completion process failed. Invalid meeting details provided.",
          HttpStatus.BAD_REQUEST);
    }
  }
}