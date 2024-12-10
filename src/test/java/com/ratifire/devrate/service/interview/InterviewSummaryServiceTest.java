package com.ratifire.devrate.service.interview;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.InterviewSummary;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.exception.InterviewSummaryNotFoundException;
import com.ratifire.devrate.repository.InterviewSummaryRepository;
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
class InterviewSummaryServiceTest {

  private static final long SUMMARY_ID = 1L;
  private static final long REVIEWER_ID = 2L;
  private static final String COMMENT = "A true feedback. You're a super developer. Keep it up!";

  @Mock
  private InterviewSummaryRepository interviewSummaryRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private InterviewSummaryService interviewSummaryService;

  private InterviewSummary summary;
  private ZonedDateTime startTime;
  private Interview interview;
  private InterviewRequest interviewerRequest;
  private InterviewRequest candidateRequest;
  private User interviewer;
  private User candidate;


  @BeforeEach
  void setUp() {
    interviewSummaryService.setUserRepository(userRepository);

    summary = InterviewSummary.builder()
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
    when(interviewSummaryRepository.findById(SUMMARY_ID)).thenReturn(Optional.of(summary));

    InterviewSummary result = interviewSummaryService.findById(SUMMARY_ID);

    assertEquals(summary, result);
  }

  @Test
  void findByIdThrowsInterviewSummaryNotFoundExceptionTest() {
    when(interviewSummaryRepository.findById(SUMMARY_ID)).thenReturn(Optional.empty());

    assertThrows(InterviewSummaryNotFoundException.class,
        () -> interviewSummaryService.findById(SUMMARY_ID));
  }

  @Test
  void saveInterviewSummaryAndParticipantsTest() {
    List<User> participants = List.of(interviewer, candidate);

    interviewSummaryService.save(summary, participants);

    verify(interviewSummaryRepository, times(1)).save(summary);
    verify(userRepository, times(1)).saveAll(participants);
  }

  @Test
  void createTest() {
    String endTime = startTime.plusMinutes(60).toString();

    interviewSummaryService.create(interview, endTime);

    assertAll(
        () -> verify(interviewSummaryRepository, times(1))
            .save(any(InterviewSummary.class)),
        () -> verify(interviewSummaryRepository).save(
            argThat(s -> s.getCandidateId() == candidate.getId()
                && s.getInterviewerId() == interviewer.getId()
                && s.getDuration() == Duration.between(startTime, ZonedDateTime.parse(endTime))
                    .toMinutes())));
  }

  @Test
  void addCommentReviewerTest() {
    when(interviewSummaryRepository.findById(SUMMARY_ID)).thenReturn(Optional.of(summary));

    InterviewRequestRole role = interviewSummaryService
        .addComment(SUMMARY_ID, REVIEWER_ID, COMMENT);

    assertAll(
        () -> assertEquals(InterviewRequestRole.CANDIDATE, role),
        () -> assertEquals(COMMENT, summary.getCandidateComment()));
  }
}