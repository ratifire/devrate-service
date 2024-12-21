package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.ParticipantRequestDto;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.mapper.impl.ParticipantRequestMapper;
import com.ratifire.devrate.sender.InterviewRequestSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Component responsible for sending interview requests to the matcher-service via a queue.
 */
@Component
@RequiredArgsConstructor
public class MatcherServiceQueueSender {

  private final InterviewRequestSender interviewRequestSender;
  private final ParticipantRequestMapper mapper;

  /**
   * Sends an {@link InterviewRequest} to the matching queue.
   *
   * @param request the interview request to process and send.
   */
  public void sendToQueue(InterviewRequest request) {
    ParticipantRequestDto dto = mapper.toDto(request);
    interviewRequestSender.send(dto);
  }
}
