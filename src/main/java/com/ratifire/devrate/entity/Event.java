package com.ratifire.devrate.entity;

import com.ratifire.devrate.enums.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an entity for storing calendar event information.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /**
   * Identifier related to the specific event type.
   * For example, if the event type is 'interview', this field will store 'interviewId'.
   * If the event type is 'conference', this field will store 'conferenceId', etc.
   */
  @Column(name = "event_type_id", nullable = false)
  private long eventTypeId;

  @Enumerated(EnumType.STRING)
  private EventType type;

  @Column(name = "room_link", nullable = false)
  private String roomLink;

  @Column(name = "host_id", nullable = false)
  private long hostId;

  @Column(name = "participant_id", columnDefinition = "bigint[]", nullable = false)
  private List<Long> participantIds;

  @Column(name = "start_time", nullable = false)
  private ZonedDateTime startTime;
}
