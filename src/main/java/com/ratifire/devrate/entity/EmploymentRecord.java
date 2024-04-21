package com.ratifire.devrate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing work experience in the system. Work experience is recorded information
 * about a user's employment history.
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employment_records")
public class EmploymentRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "start_date", nullable = false)
  @JsonFormat(pattern = "yyyy-MM")
  private LocalDate startDate;

  @Column(name = "end_date")
  @JsonFormat(pattern = "yyyy-MM")
  private LocalDate endDate;

  @Column(name = "position", nullable = false)
  private String position;

  @Column(name = "company_name", nullable = false)
  private String companyName;

  @Column(name = "description", length = 1000)
  private String description;

  @Column(name = "responsibilities", columnDefinition = "text[]")
  private List<String> responsibilities;

}
