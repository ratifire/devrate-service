package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.EventType;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * Data Transfer Object representing the calendar event.
 */
@Builder
@Getter
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
  private EventType type;
  @URL
  private String link;
  private ParticipantDto host;
  private List<ParticipantDto> participantDtos;
  private ZonedDateTime startTime;

}