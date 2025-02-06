package com.ratifire.devrate.entity.interview;

import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.InterviewRequestRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entity class representing an interview request entry.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interview_requests")
public class InterviewRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Enumerated(EnumType.STRING)
  private InterviewRequestRole role;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "mastery_id", nullable = false)
  private Mastery mastery;

  @Column(name = "desired_interview", nullable = false)
  private int desiredInterview;

  @Column(name = "average_mark", nullable = false)
  private double averageMark;    // need to be improved

  private ZonedDateTime expiredAt;

  @Column(name = "comment", length = 1000)
  private String comment;

  @ElementCollection(targetClass = ZonedDateTime.class, fetch = FetchType.LAZY)
  @CollectionTable(name = "interview_request_available_dates",
      joinColumns = @JoinColumn(name = "interview_request_id"))
  @Column(name = "available_dates", nullable = false)
  private List<ZonedDateTime> availableDates;

  @ElementCollection(targetClass = ZonedDateTime.class, fetch = FetchType.LAZY)
  @CollectionTable(name = "interview_request_assigned_dates",
      joinColumns = @JoinColumn(name = "interview_request_id"))
  @Column(name = "assigned_dates", nullable = false)
  private List<ZonedDateTime> assignedDates;

  @ElementCollection(targetClass = Integer.class, fetch = FetchType.LAZY)
  @Column(name = "black_list", nullable = false)
  private Set<Integer> blackList = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @ToString.Exclude
  private User user;
}
