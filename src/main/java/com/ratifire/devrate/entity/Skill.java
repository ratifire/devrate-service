package com.ratifire.devrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing skill in the system.
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "skills")
public class Skill {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "average_mark")
  private long averageMark;

  @Column(name = "counter")
  private long counter;

  @Column(name = "is_up", nullable = false)
  private boolean up;

}
