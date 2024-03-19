package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.ContactType;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the contact.
 */
@Builder
@Getter
public class ContactDto {

  private ContactType type;
  private String value;

}
