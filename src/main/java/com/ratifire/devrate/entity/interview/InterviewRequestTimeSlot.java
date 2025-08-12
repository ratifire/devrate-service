package com.ratifire.devrate.entity.interview;

import com.ratifire.devrate.enums.TimeSlotStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an interview request time slot entry.
 */
@Entity
@Table(name = "interview_request_time_slots")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewRequestTimeSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interview_request_id", nullable = false)
  private InterviewRequest interviewRequest;

  @Column(name = "date_time", nullable = false)
  private ZonedDateTime dateTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TimeSlotStatus status;

  @Column(name = "interview_id")
  private Long interviewId;

  @Column(name = "interview_history_id")
  private Long interviewHistoryId;
}