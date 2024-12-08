//package com.ratifire.devrate.util.interview;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.ratifire.devrate.entity.User;
//import com.ratifire.devrate.entity.interview.InterviewRequest;
//import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
//import com.ratifire.devrate.service.NotificationService;
//import com.ratifire.devrate.service.EmailService;
//import java.time.ZonedDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class InterviewCleanUpSchedulerTest {
//
//  @InjectMocks
//  private InterviewCleanUpScheduler interviewCleanUpScheduler;
//
//  @Mock
//  private InterviewRequestRepository interviewRequestRepository;
//
//  @Mock
//  private UserSecurityService userSecurityService;
//
//  @Mock
//  private NotificationService notificationService;
//
//  @Mock
//  private EmailService emailService;
//
//  @Captor
//  private ArgumentCaptor<List<InterviewRequest>> interviewRequestListCaptor;
//  private final String email = "usertest@gmail.com";
//  private User user;
//  private InterviewRequest mockRequest;
//  private List<InterviewRequest> deletedRequests;
//
//
//  @BeforeEach
//  void setUp() {
//    user = User
//        .builder()
//        .id(1L)
//        .build();
//
//    mockRequest = InterviewRequest
//        .builder()
//        .user(user)
//        .expiredAt(ZonedDateTime.now().minusDays(1))
//        .active(true)
//        .build();
//
//    deletedRequests = new ArrayList<>();
//    deletedRequests.add(mockRequest);
//  }
//
//  @Test
//  void deleteExpiredAndActiveInterviewRequestsTask_ShouldDeleteExpiredRequestsAndSendAlerts() {
//
//    when(interviewRequestRepository.findByActiveTrueAndExpiredAtBefore(any(ZonedDateTime.class)))
//        .thenReturn(deletedRequests);
//    when(userSecurityService.findEmailByUserId(anyLong()))
//        .thenReturn(email);
//
//    interviewCleanUpScheduler.deleteExpiredAndActiveInterviewRequestsTask();
//
//    verify(interviewRequestRepository, times(1)).deleteAll(interviewRequestListCaptor.capture());
//
//    List<InterviewRequest> deletedRequests = interviewRequestListCaptor.getValue();
//    assertEquals(mockRequest, deletedRequests.getFirst());
//  }
//
//  @Test
//  void deleteExpiredAndActiveInterviewRequestsTask_ShouldDoNothingIfNoExpiredRequestsTest() {
//    when(interviewRequestRepository.findByActiveTrueAndExpiredAtBefore(any(ZonedDateTime.class)))
//        .thenReturn(Collections.emptyList());
//
//    interviewCleanUpScheduler.deleteExpiredAndActiveInterviewRequestsTask();
//
//    verify(interviewRequestRepository, never()).deleteAll();
//  }
//}