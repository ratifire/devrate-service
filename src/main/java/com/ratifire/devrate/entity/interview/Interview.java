package com.ratifire.devrate.entity.interview;

import com.ratifire.devrate.enums.ConsentStatus;
import com.ratifire.devrate.enums.InterviewRequestRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an interview.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interviews")
public class Interview {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "user_id", nullable = false)
  private long userId;

  @Column(name = "mastery_id", nullable = false)
  private long masteryId;

  @Enumerated(EnumType.STRING)
  private InterviewRequestRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "consent_status", nullable = false)
  private ConsentStatus consentStatus;

  @Column(name = "event_id", nullable = false)
  private long eventId;

  @Column(name = "room_url")
  private String roomUrl;

  @Column(name = "start_time", nullable = false)
  private ZonedDateTime startTime;

  @Column(name = "language_code", nullable = false)
  private String languageCode;

  @Column(name = "request_comment")
  private String requestComment;

  @Column(name = "is_visible", nullable = false)
  private boolean isVisible;
}
