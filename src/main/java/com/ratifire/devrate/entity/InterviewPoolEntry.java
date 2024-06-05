package com.ratifire.devrate.entity;


import com.ratifire.devrate.enums.InterviewPoolEntryType;
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
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an entry in the interview pool.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interview_pool_entries")
public class InterviewPoolEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Enumerated(EnumType.STRING)
  private InterviewPoolEntryType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "specialization_id", nullable = false)
  private Specialization specialization;

  @Column(name = "is_active", nullable = false)
  private boolean active;

  private ZonedDateTime expiredAt;

  @ElementCollection
  @CollectionTable(name = "interview_time_ranges",
      joinColumns = @JoinColumn(name = "interview_pool_entry_id"))
  private List<InterviewTimeRange> dates;
}
