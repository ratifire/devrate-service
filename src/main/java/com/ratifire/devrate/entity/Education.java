package com.ratifire.devrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "educations")
public class Education {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotEmpty
  @Size(max = 100)
  @Column(nullable = false)
  private String educationType;

  @NotEmpty
  @Size(max = 100)
  @Column(nullable = false)
  private String educationName;

  @NotEmpty
  @Column(nullable = false)
  private String description;

  @NotEmpty
  @Column(nullable = false)
  private int startYear;

  @NotEmpty
  @Column(nullable = false)
  private int endYear;

  @NotNull
  @Column(name = "user_id", nullable = false)
  private long userId;

}
