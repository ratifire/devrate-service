package com.ratifire.devrate.sender;

import com.ratifire.devrate.dto.ParticipantRequestDto;
import com.ratifire.devrate.util.JsonConverter;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Component responsible for sending interview request messages to an Amazon SQS queue.
 */
@Component
@RequiredArgsConstructor
public class InterviewRequestSender {

  private final SqsTemplate sqsTemplate;

  @Value("${matching-service.sqs-endpoint}")
  private String endpoint;

  /**
   * Sends the participant interview request message to the configured Amazon SQS queue.
   *
   * @param participantRequestDto the DTO containing participant interview request details
   *                                       to be sent as a message.
   * @throws IllegalArgumentException if the DTO cannot be serialized into JSON format.
   */
  public void send(ParticipantRequestDto participantRequestDto) {
    String payload = JsonConverter.serialize(participantRequestDto);

    Message<String> message = MessageBuilder.withPayload(payload)
        .setHeader("contentType", "application/json")
        .build();

    sqsTemplate.send(endpoint, message);
  }
}
