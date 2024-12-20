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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entity class representing mastery in the system.
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "masteries")
public class Mastery {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "specialization_id", nullable = false)
  @ToString.Exclude
  private Specialization specialization;

  @Column(nullable = false)
  private int level;

  @Column(precision = 4, scale = 2)
  private BigDecimal softSkillMark;

  @Column(precision = 4, scale = 2)
  private BigDecimal hardSkillMark;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "mastery_id", nullable = false)
  private List<Skill> skills;

  @OneToMany(mappedBy = "mastery", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MasteryHistory> masteryHistories;

  @OneToMany(mappedBy = "mastery", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InterviewRequest> interviewRequests;

}
