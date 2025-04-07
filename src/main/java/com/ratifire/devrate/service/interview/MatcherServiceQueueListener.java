package com.ratifire.devrate.service.interview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.PairedParticipantDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Listener for handling messages from matcher-service.
 */
@Component
@AllArgsConstructor
public class MatcherServiceQueueListener {

  private ObjectMapper objectMapper;
  private InterviewService interviewService;

  /**
   * Processes incoming messages from the queue.
   *
   * @param message the message payload
   */
  @SqsListener("${matching-service.received-sqs-endpoint}")
  public void receiveMessage(String message) throws JsonProcessingException {
    System.out.println("Message: " + message);
    PairedParticipantDto pairedParticipantDto = objectMapper.readValue(message,
        PairedParticipantDto.class);
    System.out.println("Interview paired received: " + pairedParticipantDto);
    interviewService.create(pairedParticipantDto);
  }
}
