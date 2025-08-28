package com.ratifire.devrate.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO that represents rejection data needed to notify users that an interview was rejected.
 */
@Builder
@Getter
public class InterviewRejectedDto {

  private long recipientUserId;
  private long rejectorUserId;
  private ZonedDateTime scheduledTime;
  private long recipientInterviewId;
  private long rejectorInterviewId;
}
