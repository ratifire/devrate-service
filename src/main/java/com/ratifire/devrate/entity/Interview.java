package com.ratifire.devrate.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_entry_id", nullable = false)
  private InterviewPoolEntry candidatePoolEntry;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interviewer_entry_id", nullable = false)
  private InterviewPoolEntry interviewerPoolEntry;

  private ZonedDateTime startTime;

  private ZonedDateTime endTime;
}
