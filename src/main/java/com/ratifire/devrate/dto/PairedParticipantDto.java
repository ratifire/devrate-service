package com.ratifire.devrate.dto;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the paired interview participant.
 */
@Setter
@Getter
public class PairedParticipantDto {

  private Long interviewerId;
  private Long candidateId;
  private Long interviewerParticipantId;
  private Long candidateParticipantId;
  private ZonedDateTime date;

  @Override
  public String toString() {
    return "PairedParticipantDto{"
        + "interviewerId='" + interviewerId + '\''
        + ", candidateId='" + candidateId + '\''
        + ", interviewerParticipantId='" + interviewerParticipantId + '\''
        + ", candidateParticipantId='" + candidateParticipantId + '\''
        + ", date='" + date + '\''
        + '}';
  }
}
