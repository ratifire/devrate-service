package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewRequestRole;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * Data Transfer Object representing the calendar event.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

  private long id;
  /**
   * Identifier related to the specific event type.
   * For example, if the event type is 'interview', this field will store 'interviewId'.
   * If the event type is 'conference', this field will store 'conferenceId', etc.
   */
  private long eventTypeId;
  @URL
  private String link;
  private Participant host;
  private List<Participant> participants;
  private ZonedDateTime startTime;

  /**
   * Data Transfer Object representing the participant data of the calendar event.
   */
  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Participant {
    private String name;
    private String surname;
    private String status;
    private InterviewRequestRole role;
  }
}
