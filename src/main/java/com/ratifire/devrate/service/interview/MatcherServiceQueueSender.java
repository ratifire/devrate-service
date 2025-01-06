package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.ParticipantRequestDto;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.SqsMessageType;
import com.ratifire.devrate.mapper.impl.ParticipantRequestMapper;
import com.ratifire.devrate.sender.InterviewRequestSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handles sending interview requests to the matcher-service queue.
 */
@Component
@RequiredArgsConstructor
public class MatcherServiceQueueSender {

  private final InterviewRequestSender interviewRequestSender;
  private final ParticipantRequestMapper mapper;

  /**
   * Sends a create message to the queue.
   *
   * @param request the interview request
   */
  public void create(InterviewRequest request) {
    ParticipantRequestDto dto = mapper.toDto(request);
    interviewRequestSender.send(SqsMessageType.CREATE.name(), dto);
  }

  /**
   * Sends an update message to the queue.
   *
   * @param request the interview request
   */
  public void update(InterviewRequest request) {
    ParticipantRequestDto dto = mapper.toDto(request);
    interviewRequestSender.send(SqsMessageType.UPDATE.name(), dto);
  }
}
