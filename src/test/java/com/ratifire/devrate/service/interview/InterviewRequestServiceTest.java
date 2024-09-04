package com.ratifire.devrate.service.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewRequestServiceTest {

  @Mock
  private InterviewRequestRepository interviewRequestRepository;

  @InjectMocks
  private InterviewRequestService interviewRequestService;

  private InterviewRequest candidateRequest;
  private InterviewRequest interviewerRequest;
  private List<InterviewRequest> matchedRequests;

  @BeforeEach
  void setUp() {
    candidateRequest = InterviewRequest.builder()
        .id(1L)
        .role(InterviewRequestRole.CANDIDATE)
        .active(true)
        .build();

    interviewerRequest = InterviewRequest.builder()
        .id(2L)
        .role(InterviewRequestRole.INTERVIEWER)
        .active(false)
        .build();

    matchedRequests = List.of(candidateRequest);
  }

  @Test
  void testSaveInterviewRequest() {
    when(interviewRequestRepository.save(any(InterviewRequest.class))).thenReturn(candidateRequest);

    InterviewRequest savedRequest = interviewRequestService.save(candidateRequest);

    assertNotNull(savedRequest);
    assertEquals(candidateRequest.getId(), savedRequest.getId());
    verify(interviewRequestRepository, times(1)).save(candidateRequest);
  }

  @Test
  void testFindMatchedCandidates() {
    when(interviewRequestRepository.findMatchedCandidates(any(InterviewRequest.class),
        anyList())).thenReturn(matchedRequests);

    List<InterviewRequest> result = interviewRequestService
        .findMatchedCandidates(candidateRequest, List.of());

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(interviewRequestRepository, times(1)).findMatchedCandidates(candidateRequest, List.of());
  }

  @Test
  void testFindMatchedInterviewers() {
    when(interviewRequestRepository.findMatchedInterviewers(any(InterviewRequest.class),
        anyList())).thenReturn(matchedRequests);

    List<InterviewRequest> result = interviewRequestService
        .findMatchedInterviewers(candidateRequest, List.of());

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(interviewRequestRepository, times(1))
        .findMatchedInterviewers(candidateRequest, List.of());
  }

  @Test
  void testMarkAsNonActive() {
    interviewRequestService.markAsNonActive(candidateRequest);

    assertFalse(candidateRequest.isActive());
    verify(interviewRequestRepository, times(1)).save(candidateRequest);
  }

  @Test
  void testHandleRejectedInterview() {
    interviewRequestService.handleRejectedInterview(candidateRequest, interviewerRequest);

    assertTrue(candidateRequest.isActive());
    assertTrue(interviewerRequest.isActive());
    verify(interviewRequestRepository, times(1)).save(candidateRequest);
    verify(interviewRequestRepository, times(1)).save(interviewerRequest);
  }

  @Test
  void testDeleteInterviewRequests() {
    List<Long> ids = List.of(1L, 2L);

    interviewRequestService.deleteInterviewRequests(ids);

    verify(interviewRequestRepository, times(1)).deleteAllById(ids);
  }
}
