//package com.ratifire.devrate.service.interview;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.anyLong;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.ratifire.devrate.entity.User;
//import com.ratifire.devrate.entity.interview.Interview;
//import com.ratifire.devrate.entity.interview.InterviewRequest;
//import com.ratifire.devrate.enums.InterviewRequestRole;
//import com.ratifire.devrate.service.NotificationService;
//import com.ratifire.devrate.service.SpecializationService;
//import com.ratifire.devrate.service.UserService;
//import com.ratifire.devrate.util.interview.DateTimeUtils;
//import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
//import com.ratifire.devrate.util.zoom.service.ZoomApiService;
//import com.ratifire.devrate.util.zoom.webhook.exception.ZoomWebhookException;
//import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest;
//import java.time.ZonedDateTime;
//import java.util.List;
//import java.util.Map;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class InterviewCompletionServiceTest {
//
//  private static final long MEETING_ID = 123456789L;
//
//  @Mock
//  private InterviewService interviewService;
//
//  @Mock
//  private InterviewRequestService interviewRequestService;
//
//  @Mock
//  private InterviewSummaryService interviewSummaryService;
//
//  @Mock
//  private InterviewFeedbackDetailService interviewFeedbackDetailService;
//
//  @Mock
//  private SpecializationService specializationService;
//
//  @Mock
//  private UserService userService;
//
//  @Mock
//  private NotificationService notificationService;
//
//  @Mock
//  private ZoomApiService zoomApiService;
//  @Mock
//  private UserSecurityService userSecurityService;
//
//
//  @InjectMocks
//  private InterviewCompletionService interviewCompletionService;
//
//  private WebHookRequest.Payload.Meeting meeting;
//  private Interview interview;
//  private User interviewer;
//  private User candidate;
//
//  @BeforeEach
//  void setUp() {
//    meeting = WebHookRequest.Payload.Meeting.builder()
//        .id(String.valueOf(MEETING_ID))
//        .endTime(ZonedDateTime.now().plusMinutes(60).toString())
//        .build();
//
//    interviewer = User.builder().id(1L).build();
//    candidate = User.builder().id(2L).build();
//
//    interview = Interview.builder()
//        .id(3L)
//        .interviewerRequest(
//            InterviewRequest.builder()
//                .id(4L)
//                .user(interviewer)
//                .build())
//        .candidateRequest(
//            InterviewRequest.builder()
//                .id(5L)
//                .user(candidate)
//                .build())
//        .startTime(ZonedDateTime.now().minusMinutes(10))
//        .build();
//  }
//
//  @Test
//  void finalizeInterviewProcessWithValidMeetingShouldReturnSuccess()
//      throws ZoomApiException, ZoomWebhookException {
//    when(userSecurityService.findEmailByUserId(interviewer.getId()))
//        .thenReturn("interviewer@example.com");
//    when(userSecurityService.findEmailByUserId(candidate.getId()))
//        .thenReturn("candidate@example.com");
//
//    when(interviewService.getInterviewByMeetingId(MEETING_ID)).thenReturn(interview);
//    when(interviewSummaryService.createInterviewSummary(any(Interview.class),
//        any())).thenReturn(6L);
//    when(interviewFeedbackDetailService.saveInterviewFeedbackDetail(any(Interview.class),
//        anyLong())).thenReturn(
//        Map.of(InterviewRequestRole.CANDIDATE, 7L, InterviewRequestRole.INTERVIEWER, 8L));
//
//    interviewCompletionService.finalizeInterviewProcess(meeting);
//
//    verify(interviewSummaryService).createInterviewSummary(interview, meeting.getEndTime());
//    verify(specializationService).updateUserInterviewCounts(interview);
//    verify(userService).refreshUserInterviewCounts(List.of(interviewer, candidate));
//    verify(interviewFeedbackDetailService).saveInterviewFeedbackDetail(interview, 6L);
//
//    verify(notificationService).addInterviewFeedbackDetail(candidate,
//        7L, "candidate@example.com");
//    verify(notificationService).addInterviewFeedbackDetail(interviewer,
//        8L, "interviewer@example.com");
//
//    verify(zoomApiService).deleteMeeting(MEETING_ID);
//    verify(interviewService).deleteInterview(interview.getId());
//    verify(interviewRequestService).deleteInterviewRequests(List.of(4L, 5L));
//  }
//
//  @Test
//  void finalizeInterviewProcessWhenZoomApiExceptionOccursShouldLogError()
//  throws ZoomApiException {
//    when(interviewService.getInterviewByMeetingId(MEETING_ID)).thenReturn(interview);
//    when(interviewSummaryService.createInterviewSummary(any(Interview.class), any()))
//        .thenReturn(6L);
//    when(interviewFeedbackDetailService.saveInterviewFeedbackDetail(any(Interview.class),
//        anyLong())).thenReturn(
//        Map.of(InterviewRequestRole.CANDIDATE, 7L, InterviewRequestRole.INTERVIEWER, 8L));
//    doThrow(new ZoomApiException("Zoom API error", new Throwable())).when(zoomApiService)
//        .deleteMeeting(MEETING_ID);
//
//    assertThrows(ZoomApiException.class,
//        () -> interviewCompletionService.finalizeInterviewProcess(meeting));
//  }
//
//  @Test
//  void validateMeetingEndTimeWithInvalidMeetingShouldThrowException() {
//    meeting.setId(null);
//
//    assertThrows(ZoomWebhookException.class,
//        () -> interviewCompletionService.validateMeetingEndTime(meeting));
//  }
//
//  @Test
//  void validateMeetingEndTimeWhenWebhookTriggeredTooEarlyShouldReturnFalse()
//      throws ZoomWebhookException {
//    when(interviewService.getInterviewByMeetingId(MEETING_ID)).thenReturn(interview);
//
//    ZonedDateTime currentDateTime = interview.getStartTime().plusSeconds(9);
//
//    try (MockedStatic<DateTimeUtils> mockedStatic = mockStatic(DateTimeUtils.class)) {
//      mockedStatic.when(() -> DateTimeUtils.convertToUtcTimeZone(any(ZonedDateTime.class)))
//          .thenReturn(currentDateTime);
//
//      boolean isValid = interviewCompletionService.validateMeetingEndTime(meeting);
//
//      assertFalse(isValid);
//    }
//  }
//}
