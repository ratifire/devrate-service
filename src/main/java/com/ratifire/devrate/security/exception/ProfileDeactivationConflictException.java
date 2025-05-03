package com.ratifire.devrate.security.exception;

import com.ratifire.devrate.security.model.dto.ProfileDeactivationDto;
import lombok.Getter;

/**
 * Exception thrown when an error occurs during the profile deactivation process.
 */
@Getter
public class ProfileDeactivationConflictException extends RuntimeException {

  private final ProfileDeactivationDto dto;

  public ProfileDeactivationConflictException(ProfileDeactivationDto dto) {
    super("Profile cannot be deactivated due to existing future interviews or requests");
    this.dto = dto;
  }

}