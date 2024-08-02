package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewRequestRole;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * Data Transfer Object representing the calendar event.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventDto {

  private long id;
  @URL
  private String zoomLink;
  private Participant host;
  private List<Participant> participants;
  private LocalDateTime startTime;

  /**
   * Data Transfer Object representing the participant data of the calendar event.
   */
  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  public static class Participant {
    private String name;
    private String surname;
    private InterviewRequestRole role;
  }
}
