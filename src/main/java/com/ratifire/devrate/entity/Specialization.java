package com.ratifire.devrate.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing specialization in the system.
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "specializations")
public class Specialization {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "main_mastery_id", nullable = false)
  private int mainMasteryId;

  @Column(name = "completed_interviews")
  private int completedInterviews;

  @Column(name = "conducted_interviews")
  private int conductedInterviews;

  @Column(name = "is_main", nullable = false)
  private boolean main;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "specialization_id", nullable = false)
  private List<Mastery> masteries;
}
