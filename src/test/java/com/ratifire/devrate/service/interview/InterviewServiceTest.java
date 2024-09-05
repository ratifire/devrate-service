package com.ratifire.devrate.service.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.exception.InterviewNotFoundException;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.service.event.EventService;
import com.ratifire.devrate.util.interview.InterviewPair;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.payloads.ZoomCreateMeetingResponse;
import com.ratifire.devrate.util.zoom.service.ZoomApiService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

  @Mock
  private InterviewRepository interviewRepository;

  @Mock
  private ZoomApiService zoomApiService;

  @Mock
  private EventService eventService;

  @InjectMocks
  private InterviewService interviewService;

  private InterviewRequest candidateRequest;
  private InterviewRequest interviewerRequest;
  private InterviewPair<InterviewRequest, InterviewRequest> interviewPair;
  private ZoomCreateMeetingResponse zoomCreateMeetingResponse;

  @BeforeEach
  void setUp() {
    candidateRequest = InterviewRequest.builder()
        .role(InterviewRequestRole.CANDIDATE)
        .availableDates(List.of(ZonedDateTime.now().plusDays(2)))
        .user(User.builder().id(200L).build())
        .build();

    interviewerRequest = InterviewRequest.builder()
        .role(InterviewRequestRole.INTERVIEWER)
        .availableDates(List.of(ZonedDateTime.now().plusDays(2)))
        .user(User.builder().id(201L).build())
        .build();

    interviewPair = InterviewPair.<InterviewRequest, InterviewRequest>builder()
        .candidate(candidateRequest)
        .interviewer(interviewerRequest)
        .build();

    zoomCreateMeetingResponse = new ZoomCreateMeetingResponse();
    zoomCreateMeetingResponse.setId(123456789L);
    zoomCreateMeetingResponse.setJoinUrl("https://zoom.us/j/123456789");
  }

  @Test
  void createInterviewSuccess() {
    ZonedDateTime matchingDate = ZonedDateTime.now().plusDays(2).withHour(10).withMinute(0);
    candidateRequest.setAvailableDates(List.of(matchingDate));
    interviewerRequest.setAvailableDates(List.of(matchingDate));

    when(zoomApiService.createMeeting(anyString(), anyString(), any(ZonedDateTime.class)))
        .thenReturn(Optional.of(zoomCreateMeetingResponse));

    Optional<Interview> result = interviewService.createInterview(interviewPair);

    assertTrue(result.isPresent());
    verify(interviewRepository).save(any(Interview.class));
    verify(eventService).save(any(Event.class), anyList());

    Interview interview = result.get();
    assertEquals(candidateRequest, interview.getCandidateRequest());
    assertEquals(interviewerRequest, interview.getInterviewerRequest());
    assertEquals(zoomCreateMeetingResponse.getId(), interview.getZoomMeetingId());
    assertEquals(zoomCreateMeetingResponse.getJoinUrl(), interview.getZoomJoinUrl());
  }

  @Test
  void createInterviewThrowsNoSuchElementExceptionWhenNoMatchingDate() {
    candidateRequest.setAvailableDates(new ArrayList<>());
    interviewerRequest.setAvailableDates(new ArrayList<>());

    assertThrows(NoSuchElementException.class,
        () -> interviewService.createInterview(interviewPair));

    verify(interviewRepository, never()).save(any());
    verify(eventService, never()).save(any(), anyList());
  }

  @Test
  void deleteRejectedInterview() throws ZoomApiException {
    Interview interview = Interview.builder()
        .zoomMeetingId(123456789L)
        .zoomJoinUrl("https://zoom.us/j/123456789")
        .build();

    when(interviewRepository.findById(100L)).thenReturn(Optional.of(interview));

    Interview result = interviewService.deleteRejectedInterview(100L);

    assertNotNull(result);
    verify(interviewRepository).deleteById(100L);
    verify(eventService).deleteByEventTypeId(100L);
    verify(zoomApiService).deleteMeeting(interview.getZoomMeetingId());
  }

  @Test
  void deleteRejectedInterviewThrowsExceptionWhenNotFound() throws ZoomApiException {
    when(interviewRepository.findById(100L)).thenReturn(Optional.empty());

    assertThrows(InterviewNotFoundException.class,
        () -> interviewService.deleteRejectedInterview(100L));

    verify(interviewRepository, never()).deleteById(anyLong());
    verify(eventService, never()).deleteByEventTypeId(anyLong());
    verify(zoomApiService, never()).deleteMeeting(anyLong());
  }
}