package com.ratifire.devrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an education in the system.
 */
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
  private String type;

  @NotEmpty
  @Size(max = 100)
  private String name;

  @NotEmpty
  private String description;

  @PositiveOrZero
  private int startYear;

  @PositiveOrZero
  private int endYear;

  @NotNull
  @Column(name = "user_id", nullable = false)
  private long userId;

}
