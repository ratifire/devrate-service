package com.ratifire.devrate.entity.interview;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.InterviewRequestRole;
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
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an InterviewFeedbackDetail.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interview_feedback_details")
public class InterviewFeedbackDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "start_time", nullable = false)
  private ZonedDateTime startTime;

  @Column(name = "interview_summary_id", nullable = false)
  private long interviewSummaryId;

  @Column(name = "evaluated_mastery_id", nullable = false)
  private long evaluatedMasteryId;

  @Column(name = "skill_id", columnDefinition = "bigint[]", nullable = false)
  private List<Long> skillsIds;

  @Enumerated(EnumType.STRING)
  @Column(name = "participant_role", nullable = false)
  private InterviewRequestRole participantRole;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "participant_id", nullable = false)
  private User participant;

  @Column(name = "host_feedback_id", nullable = false)
  private long hostFeedbackId;

}