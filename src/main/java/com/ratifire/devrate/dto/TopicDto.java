package com.ratifire.devrate.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the chat topic.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TopicDto {
  private long opponentUserId;
  private String opponentFirstName;
  private String opponentLastName;
  private String opponentPicture;
  private String lastMessage;
  private ZonedDateTime lastMessageDate;
}
