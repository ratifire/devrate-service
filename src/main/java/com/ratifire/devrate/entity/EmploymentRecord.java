package com.ratifire.devrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

  @Column(name = "start_year", nullable = false)
  private int startYear;

  @Column(name = "end_year", nullable = false)
  private int endYear;

  @Column(name = "position", nullable = false)
  private String position;

  @Column(name = "company_name", nullable = false)
  private String companyName;

  @Column(name = "description", length = 1000)
  private String description;

  @Column(name = "responsibilities", columnDefinition = "text[]")
  private List<String> responsibilities;

}
