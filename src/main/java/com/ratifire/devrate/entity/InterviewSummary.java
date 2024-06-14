package com.ratifire.devrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an interview summary in the system.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interview_summaries")
public class InterviewSummary {

  @Id
  private long id;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private long duration;

  @Column(name = "candidate_id", nullable = false)
  private long candidateId;

  @Column(name = "interviewer_id", nullable = false)
  private long interviewerId;

}
