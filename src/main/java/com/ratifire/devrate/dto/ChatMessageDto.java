package com.ratifire.devrate.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the chat message.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatMessageDto {
  private long senderId;
  private long receiverId;
  private String payload;
  private String status;
  private ZonedDateTime dateTime;
  private Long readMessageId;    // TODO: will be used
}
