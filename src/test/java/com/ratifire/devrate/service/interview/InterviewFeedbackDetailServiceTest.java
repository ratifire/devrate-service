package com.ratifire.devrate.service.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.InterviewFeedbackDetailDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewFeedbackDetail;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.exception.InterviewFeedbackDetailNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.interview.InterviewFeedbackDetailRepository;
import com.ratifire.devrate.service.specialization.SkillService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewFeedbackDetailServiceTest {

  @Mock
  private InterviewFeedbackDetailRepository feedbackDetailRepository;

  @Mock
  private DataMapper<InterviewFeedbackDetailDto, InterviewFeedbackDetail> feedbackDetailMapper;

  @Mock
  private SkillService skillService;

  @InjectMocks
  private InterviewFeedbackDetailService feedbackDetailService;

  private static final long FEEDBACK_ID = 1L;
  private static final List<Long> SKILL_IDS = List.of(101L, 102L);

  @Test
  void getInterviewFeedbackDetailWhenExistsThenReturnFeedbackDetailDto() {
    InterviewFeedbackDetail feedbackDetail = InterviewFeedbackDetail.builder()
        .id(FEEDBACK_ID)
        .startTime(ZonedDateTime.now())
        .skillsIds(SKILL_IDS)
        .build();

    InterviewFeedbackDetailDto feedbackDetailDto = InterviewFeedbackDetailDto.builder()
        .interviewStartTime(feedbackDetail.getStartTime())
        .skills(List.of())
        .build();

    when(feedbackDetailRepository.findById(FEEDBACK_ID)).thenReturn(Optional.of(feedbackDetail));
    when(skillService.findAllById(SKILL_IDS)).thenReturn(List.of());
    when(feedbackDetailMapper.toDto(feedbackDetail, List.of())).thenReturn(feedbackDetailDto);

    InterviewFeedbackDetailDto result = feedbackDetailService
        .getInterviewFeedbackDetail(FEEDBACK_ID);

    assertNotNull(result);
    assertEquals(feedbackDetailDto, result);
    verify(feedbackDetailRepository).findById(FEEDBACK_ID);
    verify(skillService).findAllById(SKILL_IDS);
    verify(feedbackDetailMapper).toDto(feedbackDetail, List.of());
  }

  @Test
  void getInterviewFeedbackDetailWhenNotFoundThenThrowException() {
    when(feedbackDetailRepository.findById(FEEDBACK_ID)).thenReturn(Optional.empty());

    assertThrows(InterviewFeedbackDetailNotFoundException.class, () -> feedbackDetailService
        .getInterviewFeedbackDetail(FEEDBACK_ID));

    verify(feedbackDetailRepository).findById(FEEDBACK_ID);
  }

  @Test
  void saveInterviewFeedbackDetailThenReturnSavedIds() {
    Interview interview = mock(Interview.class);
    InterviewRequest candidateRequest = mock(InterviewRequest.class);
    InterviewRequest interviewerRequest = mock(InterviewRequest.class);
    Mastery candidateMastery = mock(Mastery.class);
    Mastery interviewerMastery = mock(Mastery.class);

    when(candidateRequest.getMastery()).thenReturn(candidateMastery);
    when(interviewerRequest.getMastery()).thenReturn(interviewerMastery);

    when(candidateMastery.getSkills()).thenReturn(List.of(mock(Skill.class)));
    when(interviewerMastery.getSkills()).thenReturn(List.of(mock(Skill.class)));

    when(interview.getCandidateRequest()).thenReturn(candidateRequest);
    when(interview.getInterviewerRequest()).thenReturn(interviewerRequest);
    when(interview.getStartTime()).thenReturn(ZonedDateTime.now());

    when(feedbackDetailRepository.save(any())).thenAnswer(invocation -> {
      InterviewFeedbackDetail savedDetail = invocation.getArgument(0);
      savedDetail.setId(FEEDBACK_ID);
      return savedDetail;
    });

    Map<String, Long> result = feedbackDetailService.saveInterviewFeedbackDetail(interview, 100L);

    assertEquals(FEEDBACK_ID, result.get("candidateFeedbackId"));
    assertEquals(FEEDBACK_ID, result.get("interviewerFeedbackId"));

    verify(feedbackDetailRepository, times(2)).save(any(InterviewFeedbackDetail.class));
  }

  @Test
  void deleteInterviewFeedbackDetailById() {
    feedbackDetailService.deleteById(FEEDBACK_ID);

    verify(feedbackDetailRepository).deleteById(FEEDBACK_ID);
  }

  @Test
  void deleteExpiredInterviewFeedbackDetailsTaskTest() {
    int recordRetentionPeriodInMonths = 1;
    ZonedDateTime expiredRecordsCutoffDate = ZonedDateTime.now()
        .minusMonths(recordRetentionPeriodInMonths);

    feedbackDetailService.deleteExpiredInterviewFeedbackDetailsTask();

    verify(feedbackDetailRepository, times(1))
        .deleteExpiredFeedbackDetails(any(ZonedDateTime.class));
  }

}