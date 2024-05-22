package com.ratifire.devrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing niche level history in the system.
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "niche_level_histories")
public class NicheLevelHistory {

  @EmbeddedId
  private NicheLevelHistoryId id;

  @Column(name = "change_date", nullable = false)
  private LocalDateTime changeDate;
}