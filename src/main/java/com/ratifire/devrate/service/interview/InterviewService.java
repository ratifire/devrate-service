package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.EventType;
import com.ratifire.devrate.exception.InterviewNotFoundException;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.service.EventService;
import com.ratifire.devrate.util.InterviewPair;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.service.ZoomApiService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling Interview operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {

  private final ZoomApiService zoomApiService;
  private final EventService eventService;
  private final InterviewRepository interviewRepository;

  /**
   * Retrieves an interview by its associated Zoom meeting ID.
   *
   * @param meetingId the Zoom meeting ID associated with the interview
   * @return the Interview associated with the given meeting ID
   * @throws InterviewNotFoundException if no interview is found for the provided meeting ID
   */
  public Interview getByMeetingId(long meetingId) {
    return interviewRepository.findByZoomMeetingId(meetingId)
        .orElseThrow(() -> new InterviewNotFoundException(
            String.format("Interview with meetingId %d not found.", meetingId)));
  }

  /**
   * Creates an interview based on the matched pair of candidate and interviewer.
   *
   * @param interviewPair the matched pair of candidate and interviewer
   * @return an Optional containing the created Interview if the Zoom meeting was successfully
   *     created, otherwise an empty Optional
   */
  public Optional<Interview> create(InterviewPair interviewPair) {
    InterviewRequest candidate = interviewPair.candidate();
    InterviewRequest interviewer = interviewPair.interviewer();

    ZonedDateTime matchedStartTime = getMatchedStartTime(candidate.getAvailableDates(),
        interviewer.getAvailableDates());

    return zoomApiService
        .createMeeting("Topic", "Agenda", matchedStartTime)
        .map(zoomMeeting -> {
          Interview interview = Interview.builder()
              .candidateRequest(candidate)
              .interviewerRequest(interviewer)
              .startTime(matchedStartTime)
              .zoomMeetingId(zoomMeeting.id)
              .zoomJoinUrl(zoomMeeting.getJoinUrl())
              .build();
          interviewRepository.save(interview);

          Event interviewEvent = Event.builder()
              .type(EventType.INTERVIEW)
              .roomLink(zoomMeeting.getJoinUrl())
              .hostId(interviewer.getUser().getId())
              .participantIds(List.of(candidate.getUser().getId()))
              .startTime(matchedStartTime)
              .eventTypeId(interview.getId())
              .build();
          eventService.save(interviewEvent, List.of(interviewer.getUser(), candidate.getUser()));

          return interview;
        })
        .or(() -> {
          log.debug("Interview for candidate {} and interviewer {} was not created",
              candidate, interviewer);
          return Optional.empty();
        });
  }

  /**
   * Deletes a rejected interview by its ID and returns the deleted interview.
   *
   * @param interviewId the ID of the interview to be deleted
   * @return the deleted Interview object
   * @throws InterviewNotFoundException if no interview with the specified ID is found
   */
  @Transactional
  public Interview deleteRejected(long interviewId) {
    Interview rejected = interviewRepository.findById(interviewId)
        .orElseThrow(() -> new InterviewNotFoundException(interviewId));

    interviewRepository.deleteById(interviewId);
    eventService.deleteByEventTypeId(interviewId);

    try {
      zoomApiService.deleteMeeting(rejected.getZoomMeetingId());
    } catch (ZoomApiException e) {
      log.info("Zoom API exception occurred while trying to delete the meeting with meetingId: "
              + "{}. {}", rejected.getZoomMeetingId(), e.getMessage());
    }

    return rejected;
  }

  /**
   * Deletes an interview by its ID.
   *
   * @param id The ID of the interview to delete.
   */
  public void delete(long id) {
    interviewRepository.deleteById(id);
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
   * Retrieves an Interview object by its unique identifier.
   *
   * @param id the unique identifier of the Interview to retrieve
   * @return the Interview object associated with the given id
   */
  public Interview getById(long id) {
    return interviewRepository.findById(id).orElseThrow(() -> new InterviewNotFoundException(id));
  }
}
