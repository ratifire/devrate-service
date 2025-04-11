package com.ratifire.devrate.entity.interview;

import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.ConsentStatus;
import com.ratifire.devrate.enums.InterviewRequestRole;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
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

  @Enumerated(EnumType.STRING)
  @Column(name = "consent_status", nullable = false)
  private ConsentStatus consentStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mastery_id", nullable = false)
  private Mastery mastery;

  @Column(name = "desired_interview", nullable = false)
  private int desiredInterview;

  @Column(name = "matched_interview", nullable = false)
  private int matchedInterview;

  @Column(name = "average_mark", nullable = false)
  private double averageMark;    // need to be improved

  private ZonedDateTime expiredAt;

  @Column(name = "comment", length = 1000)
  private String comment;

  @Column(name = "language_code", nullable = false)
  private String languageCode;

  @OneToMany(mappedBy = "interviewRequest", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InterviewRequestTimeSlot> timeSlots;

  @ElementCollection(targetClass = Integer.class, fetch = FetchType.LAZY)
  @Column(name = "black_list", nullable = false)
  private Set<Integer> blackList = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @ToString.Exclude
  private User user;
}
