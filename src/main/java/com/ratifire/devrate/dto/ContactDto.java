package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.ContactType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the contact.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {

  private ContactType type;
  private String value;

}
