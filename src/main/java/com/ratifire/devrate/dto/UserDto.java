package com.ratifire.devrate.dto;

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

  @NotNull
  private long id;

  @NotBlank(message = "First name cannot be blank")
  @Size(max = 100)
  @Pattern(regexp = "^[a-zA-Zа-щА-ЩґҐєЄіІїЇьЬ\\s\\-']+$", message = "First name can only contain "
      + "letters, spaces, hyphens, and apostrophes")
  private String firstName;

  @NotBlank(message = "Last name cannot be blank")
  @Size(max = 100)
  @Pattern(regexp = "^[a-zA-Zа-щА-ЩґҐєЄіІїЇьЬ\\s\\-']+$", message = "Last name can only contain "
      + "letters, spaces, hyphens, and apostrophes")
  private String lastName;

  @Size(max = 50)
  private String status;

  @NotBlank(message = "Country name cannot be blank")
  @Size(max = 100)
  private String country;

  @Size(max = 100)
  @Pattern(regexp = "^[a-zA-Zа-щА-ЩґҐєЄіІїЇьЬ\\s\\-']*$", message = "City name can only contain "
      + "letters, spaces, hyphens, and apostrophes")
  private String city;

  private boolean subscribed;

  private String description;

  private BigDecimal hardSkillMark;

  private BigDecimal softSkillMark;

  private int completedInterviews;

  private int conductedInterviews;
}
