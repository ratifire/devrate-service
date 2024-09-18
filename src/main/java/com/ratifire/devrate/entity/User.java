package com.ratifire.devrate.entity;

import com.ratifire.devrate.entity.interview.InterviewRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing an entity for storing user personal information.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  private String status;

  @Column(nullable = false)
  private String country;

  private String city;

  @Column(name = "is_subscribed", nullable = false)
  private boolean subscribed;

  @Column(length = 480)
  private String description;

  @Column(columnDefinition = "TEXT")
  private String picture;

  @Column(name = "completed_interviews")
  private int completedInterviews;

  @Column(name = "conducted_interviews")
  private int conductedInterviews;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "user_id", nullable = false)
  private List<Contact> contacts;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "user_id", nullable = false)
  private List<Education> educations;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id ASC")
  @JoinColumn(name = "user_id", nullable = false)
  private List<Achievement> achievements;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<Notification> notifications;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "user_id", nullable = false)
  private List<EmploymentRecord> employmentRecords;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "user_id", nullable = false)
  private List<LanguageProficiency> languageProficiencies;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "user_id", nullable = false)
  private List<Bookmark> bookmarks;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<Specialization> specializations;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<InterviewRequest> interviewRequests;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "interview_summaries_users",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "interview_summary_id")
  )
  private List<InterviewSummary> interviewSummaries;

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(
      name = "user_event",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "event_id")
  )
  private List<Event> events;
}
