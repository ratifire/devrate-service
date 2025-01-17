package com.ratifire.devrate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Data Transfer Object (DTO) representing the chat message.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatMessageDto {
  private long sender;
  private String receiver;
  private String payload;
  private String topicName;
  private String status;
  private ZonedDateTime dateTime;
}
