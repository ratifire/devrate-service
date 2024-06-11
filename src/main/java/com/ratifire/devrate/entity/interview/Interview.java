package com.ratifire.devrate.entity.interview;


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
  @JoinColumn(name = "candidate_request_id", nullable = false)
  private InterviewRequest candidateRequest;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interviewer_request_id", nullable = false)
  private InterviewRequest interviewerRequest;

  private ZonedDateTime startTime;
}
