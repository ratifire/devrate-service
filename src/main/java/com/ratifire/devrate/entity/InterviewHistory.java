package com.ratifire.devrate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

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

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private long duration;

  @Column(name = "user_id", nullable = false)
  private long userId;

  @ElementCollection
  @CollectionTable(name = "soft_skills", joinColumns = @JoinColumn(name = "interview_history_id"))
  @MapKeyColumn(name = "skill_name")
  @Column(name = "skill_value")
  private Map<String, String> softSkills;

  @ElementCollection
  @CollectionTable(name = "hard_skills", joinColumns = @JoinColumn(name = "interview_history_id"))
  @MapKeyColumn(name = "skill_name")
  @Column(name = "skill_value")
  private Map<String, String> hardSkills;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(nullable = false, length = 100)
  private String role;

  @Column(name = "attendee_id", nullable = false)
  private int attendeeId;

  @Column(length = 1000)
  private String feedback;
}
