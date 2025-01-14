package com.ratifire.devrate.entity;

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
import java.time.LocalDateTime;
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

  @ElementCollection
  @CollectionTable(name = "soft_skills", joinColumns = @JoinColumn(name = "interview_history_id"))
  @MapKeyColumn(name = "skill_name")
  @Column(name = "skill_value")
  private Map<String, Integer> softSkills;

  @ElementCollection
  @CollectionTable(name = "hard_skills", joinColumns = @JoinColumn(name = "interview_history_id"))
  @MapKeyColumn(name = "skill_name")
  @Column(name = "skill_value")
  private Map<String, Integer> hardSkills;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Enumerated(EnumType.STRING)
  private InterviewRequestRole role;

  @Column(name = "attendee_id", nullable = false)
  private int attendeeId;

  @Column(length = 1000)
  private String feedback;
}
