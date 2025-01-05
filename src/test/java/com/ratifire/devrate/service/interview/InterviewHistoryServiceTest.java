package com.ratifire.devrate.service.interview;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.InterviewHistory;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.exception.InterviewHistoryNotFoundException;
import com.ratifire.devrate.repository.InterviewHistoryRepository;
import com.ratifire.devrate.repository.UserRepository;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewHistoryServiceTest {

  private static final long SUMMARY_ID = 1L;
  private static final long REVIEWER_ID = 2L;
  private static final String COMMENT = "A true feedback. You're a super developer. Keep it up!";

  @Mock
  private InterviewHistoryRepository interviewHistoryRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private InterviewHistoryService interviewHistoryService;

  private InterviewHistory summary;
  private ZonedDateTime startTime;
  private Interview interview;
  private InterviewRequest interviewerRequest;
  private InterviewRequest candidateRequest;
  private User interviewer;
  private User candidate;


  @BeforeEach
  void setUp() {
    interviewHistoryService.setUserRepository(userRepository);

    summary = InterviewHistory.builder()
        .candidateId(REVIEWER_ID)
        .build();

    interviewer = User.builder()
        .interviewSummaries(new ArrayList<>())
        .build();

    candidate = User.builder()
        .interviewSummaries(new ArrayList<>())
        .build();

    interviewerRequest = InterviewRequest.builder()
        .user(interviewer)
        .build();

    candidateRequest = InterviewRequest.builder()
        .user(candidate)
        .build();

    startTime = ZonedDateTime.now();

    interview = Interview.builder()
        .interviewerRequest(interviewerRequest)
        .candidateRequest(candidateRequest)
        .startTime(startTime)
        .build();
  }

  @Test
  void findByIdTest() {
    when(interviewHistoryRepository.findById(SUMMARY_ID)).thenReturn(Optional.of(summary));

    InterviewHistory result = interviewHistoryService.findById(SUMMARY_ID);

    assertEquals(summary, result);
  }

  @Test
  void findByIdThrowsInterviewSummaryNotFoundExceptionTest() {
    when(interviewHistoryRepository.findById(SUMMARY_ID)).thenReturn(Optional.empty());

    assertThrows(InterviewHistoryNotFoundException.class,
        () -> interviewHistoryService.findById(SUMMARY_ID));
  }

  @Test
  void saveInterviewSummaryAndParticipantsTest() {
    List<User> participants = List.of(interviewer, candidate);

    interviewHistoryService.save(summary, participants);

    verify(interviewHistoryRepository, times(1)).save(summary);
    verify(userRepository, times(1)).saveAll(participants);
  }

  @Test
  void createTest() {
    String endTime = startTime.plusMinutes(60).toString();

    interviewHistoryService.create(interview, endTime);

    assertAll(
        () -> verify(interviewHistoryRepository, times(1))
            .save(any(InterviewHistory.class)),
        () -> verify(interviewHistoryRepository).save(
            argThat(s -> s.getCandidateId() == candidate.getId()
                && s.getInterviewerId() == interviewer.getId()
                && s.getDuration() == Duration.between(startTime, ZonedDateTime.parse(endTime))
                    .toMinutes())));
  }

  @Test
  void addCommentReviewerTest() {
    when(interviewHistoryRepository.findById(SUMMARY_ID)).thenReturn(Optional.of(summary));

    InterviewRequestRole role = interviewHistoryService
        .addComment(SUMMARY_ID, REVIEWER_ID, COMMENT);

    assertAll(
        () -> assertEquals(InterviewRequestRole.CANDIDATE, role),
        () -> assertEquals(COMMENT, summary.getCandidateComment()));
  }
}