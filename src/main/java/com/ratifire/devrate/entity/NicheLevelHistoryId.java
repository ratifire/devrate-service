package com.ratifire.devrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing niche level history in the system.
 */
@Embeddable
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NicheLevelHistoryId implements Serializable {

  @Column(name = "niche_id", nullable = false)
  private Long nicheId;

  @Column(name = "niche_level_id", nullable = false)
  private Long nicheLevelId;
}