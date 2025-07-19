package com.ratifire.devrate.entity;

import com.ratifire.devrate.entity.interview.InterviewHistory;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.security.model.enums.AccountLanguage;
import com.ratifire.devrate.security.model.enums.RegistrationSourceType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import java.time.ZonedDateTime;
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

  private String country;

  private String city;

  @Column(name = "is_subscribed")
  private Boolean subscribed;

  @Column(length = 480)
  private String description;

  @Column(columnDefinition = "TEXT")
  private String picture;

  @Column(name = "completed_interviews")
  private int completedInterviews;

  @Column(name = "conducted_interviews")
  private int conductedInterviews;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_language", nullable = false)
  private AccountLanguage accountLanguage;

  @Enumerated(EnumType.STRING)
  @Column(name = "registration_source", nullable = false)
  private RegistrationSourceType registrationSource;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "user_id", nullable = false)
  private List<Contact> contacts;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("startYear DESC,endYear DESC, id ASC")
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
  @OrderBy("startYear DESC,endYear DESC, id ASC")
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

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(
      name = "interview_histories_users",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "interview_history_id")
  )
  private List<InterviewHistory> interviewHistories;

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(
      name = "user_event",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "event_id")
  )
  private List<Event> events;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<Feedback> feedbacks;
}
