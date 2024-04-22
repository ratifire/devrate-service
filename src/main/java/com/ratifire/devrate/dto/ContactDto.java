package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.ContactType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the contact.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ContactDto {

  private Long id;

  @NotNull
  private ContactType type;

  @NotNull
  @Size(max = 100)
  private String value;

}
