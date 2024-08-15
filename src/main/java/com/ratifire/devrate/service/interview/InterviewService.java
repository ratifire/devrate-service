package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.exception.InterviewNotFoundException;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.util.interview.InterviewPair;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.service.ZoomApiService;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for handling Interview operations.
 */
@Service
@RequiredArgsConstructor
public class InterviewService {

  private final InterviewRepository interviewRepository;
  private final ZoomApiService zoomApiService;
  private static final Logger logger = LoggerFactory.getLogger(InterviewService.class);

  /**
   * Creates an interview based on the matched pair of candidate and interviewer.
   *
   * @param interviewPair the matched pair of candidate and interviewer
   * @return an Optional containing the created Interview if the Zoom meeting was successfully
   *     created, otherwise an empty Optional
   */
  public Optional<Interview> createInterview(
      InterviewPair<InterviewRequest, InterviewRequest> interviewPair) {
    InterviewRequest candidate = interviewPair.getCandidate();
    InterviewRequest interviewer = interviewPair.getInterviewer();

    return zoomApiService.createMeeting("Topic", "Agenda", LocalDateTime.now())
        .map(zoomMeeting -> {
          Interview interview = Interview.builder()
              .candidateRequest(candidate)
              .interviewerRequest(interviewer)
              .startTime(
                  getMatchedStartTime(candidate.getAvailableDates(),
                      interviewer.getAvailableDates()))
              .zoomMeetingId(zoomMeeting.id)
              .build();

          interviewRepository.save(interview);
          return interview;
        })
        .or(() -> {
          logger.debug("Interview for candidate {} and interviewer {} was not created",
              candidate, interviewer);
          return Optional.empty();
        });
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

  /**
   * Deletes a rejected interview by its ID and returns the deleted interview.
   *
   * @param interviewId the ID of the interview to be deleted
   * @return the deleted Interview object
   * @throws InterviewNotFoundException if no interview with the specified ID is found
   */
  public Interview deleteRejectedInterview(long interviewId) {
    Interview rejected = interviewRepository.findById(interviewId)
        .orElseThrow(() -> new InterviewNotFoundException(interviewId));

    interviewRepository.deleteById(interviewId);

    try {
      zoomApiService.deleteMeeting(rejected.getZoomMeetingId());
    } catch (ZoomApiException e) {
      logger.info("Zoom API exception occurred while trying to delete the meeting with meetingId: "
              + "{}. {}", rejected.getZoomMeetingId(), e.getMessage());
    }

    return rejected;
  }

  /**
   * Retrieves an interview by its associated Zoom meeting ID.
   *
   * @param meetingId the Zoom meeting ID associated with the interview
   * @return the Interview associated with the given meeting ID
   * @throws InterviewNotFoundException if no interview is found for the provided meeting ID
   */
  public Interview getInterviewByMeetingId(long meetingId) {
    return interviewRepository.findByZoomMeetingId(meetingId)
        .orElseThrow(() -> new InterviewNotFoundException(
            String.format("Interview with meetingId %d not found.", meetingId)));
  }

}
