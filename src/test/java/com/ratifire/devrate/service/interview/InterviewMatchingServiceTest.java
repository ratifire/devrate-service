package com.ratifire.devrate.service.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.util.interview.InterviewPair;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewMatchingServiceTest {

  @Mock
  private InterviewRequestService interviewRequestService;

  @Mock
  private InterviewService interviewService;

  @InjectMocks
  private InterviewMatchingService interviewMatchingService;

  private InterviewRequest candidateRequest;
  private InterviewRequest interviewerRequest;
  private InterviewPair<InterviewRequest, InterviewRequest> interviewPair;
  private Interview interview;

  @BeforeEach
  void setUp() {
    candidateRequest = InterviewRequest.builder()
        .role(InterviewRequestRole.CANDIDATE)
        .user(User.builder().id(200L).build())
        .availableDates(List.of(ZonedDateTime.now().plusDays(1)))
        .build();

    interviewerRequest = InterviewRequest.builder()
        .role(InterviewRequestRole.INTERVIEWER)
        .user(User.builder().id(201L).build())
        .availableDates(List.of(ZonedDateTime.now().plusDays(2)))
        .build();

    interviewPair = InterviewPair.<InterviewRequest, InterviewRequest>builder()
        .candidate(candidateRequest)
        .interviewer(interviewerRequest)
        .build();

    interview = Interview.builder()
        .candidateRequest(candidateRequest)
        .interviewerRequest(interviewerRequest)
        .zoomMeetingId(123456789L)
        .build();
  }

  @Test
  void matchSuccessWithCommonAvailableDates() {
    ZonedDateTime commonDate = ZonedDateTime.now().plusDays(1);

    candidateRequest.setAvailableDates(List.of(commonDate));
    interviewerRequest.setAvailableDates(List.of(commonDate));

    when(interviewRequestService.findMatchedCandidates(any(),
        anyList())).thenReturn(List.of(candidateRequest));
    when(interviewService.createInterview(any())).thenReturn(Optional.of(interview));

    Optional<Interview> result = interviewMatchingService.match(interviewerRequest);

    assertTrue(result.isPresent());
    verify(interviewService, times(1)).createInterview(any());

    Interview createdInterview = result.get();
    assertEquals(candidateRequest, createdInterview.getCandidateRequest());
    assertEquals(interviewerRequest, createdInterview.getInterviewerRequest());
  }

  @Test
  void matchNoMatchingCandidate() {
    when(interviewRequestService.findMatchedCandidates(any(),
        anyList())).thenReturn(List.of());

    Optional<Interview> result = interviewMatchingService.match(interviewerRequest);

    assertFalse(result.isPresent());
    verify(interviewRequestService, never()).markAsNonActive(any());
    verify(interviewService, never()).createInterview(any());
  }

  @Test
  void matchNoMatchingInterviewer() {
    when(interviewRequestService.findMatchedInterviewers(any(),
        anyList())).thenReturn(List.of());

    Optional<Interview> result = interviewMatchingService.match(candidateRequest);

    assertFalse(result.isPresent());
    verify(interviewRequestService, never()).markAsNonActive(any());
    verify(interviewService, never()).createInterview(any());
  }

  @Test
  void matchFailureOnCreateInterview() {
    when(interviewRequestService.findMatchedCandidates(any(),
        anyList())).thenReturn(List.of(candidateRequest));
    when(interviewService.createInterview(any())).thenReturn(Optional.empty());

    Optional<Interview> result = interviewMatchingService.match(interviewerRequest);

    assertFalse(result.isPresent());
    verify(interviewRequestService, never()).markAsNonActive(any());
    verify(interviewService, times(1)).createInterview(any());
  }

  @Test
  void markPairAsNonActive() {
    interviewMatchingService.markPairAsNonActive(interviewPair);

    verify(interviewRequestService, times(1)).markAsNonActive(candidateRequest);
    verify(interviewRequestService, times(1)).markAsNonActive(interviewerRequest);
  }
}

