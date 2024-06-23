package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.util.zoom.service.ZoomApiService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

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
     * Creates a new interview based on the provided candidate and interviewer requests.
     *
     * @param candidate   The request details for the candidate.
     * @param interviewer The request details for the interviewer.
     */
    public void createInterview(InterviewRequest candidate, InterviewRequest interviewer) {
        zoomApiService.createMeeting("Topic", "Agenda", LocalDateTime.now())
                .ifPresentOrElse(zoomMeeting -> {
                    Interview interview = Interview.builder()
                            .candidateRequest(candidate)
                            .interviewerRequest(interviewer)
                            .startTime(
                                    getMatchedStartTime(candidate.getAvailableDates(), interviewer.getAvailableDates()))
                            .zoomMeetingId(zoomMeeting.id)
                            .build();

                    interviewRepository.save(interview);
                }, () -> logger.debug("Interview for candidate {} and interviewer {} was not created",
                        candidate, interviewer));
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
