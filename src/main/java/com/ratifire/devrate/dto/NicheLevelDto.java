package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.NicheLevelName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for niche.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class NicheLevelDto {

  private Long id;

  private NicheLevelName nicheLevelName;

}
