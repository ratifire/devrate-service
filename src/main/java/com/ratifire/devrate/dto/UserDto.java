package com.ratifire.devrate.dto;

import com.ratifire.devrate.security.model.enums.AccountLanguage;
import com.ratifire.devrate.security.model.enums.RegistrationSourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the user personal info.
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class UserDto {

  private static final String NAME_PATTERN = "^[\\p{L}\\s\\-']+$";
  private static final String CITY_PATTERN = "^[\\p{L}\\s\\-']+$";

  @NotNull
  private long id;

  @NotBlank(message = "First name cannot be blank")
  @Size(max = 50)
  @Pattern(regexp = NAME_PATTERN, message = "First name can only contain letters, spaces, hyphens,"
      + " and apostrophes")
  private String firstName;

  @NotBlank(message = "Last name cannot be blank")
  @Size(max = 50)
  @Pattern(regexp = NAME_PATTERN, message = "Last name can only contain letters, spaces, hyphens,"
      + " and apostrophes")
  private String lastName;

  @Size(max = 50)
  private String status;

  @NotBlank(message = "Country name cannot be blank")
  @Size(max = 100)
  private String country;

  @Size(max = 100)
  @Pattern(regexp = CITY_PATTERN, message = "City name can only contain letters, spaces, hyphens,"
      + " and apostrophes")
  private String city;

  private boolean subscribed;

  @Size(max = 480)
  private String description;

  private BigDecimal hardSkillMark;

  private BigDecimal softSkillMark;

  private int completedInterviews;

  private int conductedInterviews;

  private Boolean accountActivated;

  private RegistrationSourceType registrationSource;

  private AccountLanguage accountLanguage;
}
