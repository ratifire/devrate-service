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
 * Entity class representing an achievement in the system.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "achievements")
public class Achievement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String link;

  @Column(nullable = false)
  private String summary;

  @Column(nullable = false)
  private String description;
}
