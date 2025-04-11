package com.ratifire.devrate.entity.interview;

import com.ratifire.devrate.enums.ConsentStatus;
import com.ratifire.devrate.enums.InterviewRequestRole;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an InterviewHistory in the system.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interview_histories")
public class InterviewHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "date_time", nullable = false)
  private ZonedDateTime dateTime;

  @Column(nullable = false)
  private long duration;

  @Column(name = "user_id", nullable = false)
  private long userId;

  @Column(name = "mastery_id", nullable = false)
  private long masteryId;

  @ElementCollection
  @CollectionTable(name = "soft_skills", joinColumns = @JoinColumn(name = "interview_history_id"))
  @MapKeyColumn(name = "skill_name")
  @Column(name = "skill_value", precision = 4, scale = 2)
  private Map<String, BigDecimal> softSkills;

  @ElementCollection
  @CollectionTable(name = "hard_skills", joinColumns = @JoinColumn(name = "interview_history_id"))
  @MapKeyColumn(name = "skill_name")
  @Column(name = "skill_value", precision = 4, scale = 2)
  private Map<String, BigDecimal> hardSkills;

  @Column(nullable = false)
  private String specialization;

  @Column(name = "mastery_level", nullable = false)
  private int masteryLevel;

  @Enumerated(EnumType.STRING)
  private InterviewRequestRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "consent_status", nullable = false)
  private ConsentStatus consentStatus;

  @Column(name = "attendee_id", nullable = false)
  private long attendeeId;

  @Column(name = "attendee_first_name", nullable = false)
  private String attendeeFirstName;

  @Column(name = "attendee_last_name", nullable = false)
  private String attendeeLastName;

  @Column(name = "attendee_mastery_level", nullable = false)
  private int attendeeMasteryLevel;

  @Column(name = "attendee_specialization", nullable = false)
  private String attendeeSpecialization;

  @Column(length = 1000)
  private String feedback;

  @Column(name = "is_visible", nullable = false)
  private Boolean isVisible;

  @Column(name = "interview_id")
  private long interviewId;
}
