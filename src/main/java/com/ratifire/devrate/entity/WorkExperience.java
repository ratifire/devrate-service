package com.ratifire.devrate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing work experience in the system. Work experience is recorded information
 * about a user's employment history.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "work_experience")
public class WorkExperience {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "start_date", nullable = false)
  @JsonFormat(pattern = "yyyy")
  private LocalDate startDate;

  @Column(name = "end_date")
  @JsonFormat(pattern = "yyyy")
  private LocalDate endDate;

  @Column(name = "position", nullable = false)
  private String position;

  @Column(name = "company_name", nullable = false)
  private String companyName;

  @Column(name = "description", length = 1000)
  private String description;

  @Column(name = "user_id", nullable = false)
  private long userId;

  @Column(name = "responsibility_id")
  private String responsibilityId;

}
