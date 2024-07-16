package com.ratifire.devrate.configuration;


import com.ratifire.devrate.service.AchievementService;
import com.ratifire.devrate.service.EducationService;
import com.ratifire.devrate.service.OwnershipResourceVerifiable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;

/**
 * Configuration class for setting up the resource ownership verification beans.
 */
@Configuration
public class ResourceOwnershipVerificationConfiguration {

  /**
   * Creates a bean that maps resource types to their corresponding ownership verification
   * services.
   */
  @Bean("ownershipCheckers")
  public Map<String, OwnershipResourceVerifiable> ownershipCheckers(
      AchievementService achievementService,
      EducationService educationService
  ) {
    Map<String, OwnershipResourceVerifiable> map = new HashMap<>();
    map.put("achievements", achievementService);
    map.put("educations", educationService);

    return map;
  }

  @Bean
  public AuthorizationDecision positiveAuthorizationDecision() {
    return new AuthorizationDecision(true);
  }

  @Bean
  public AuthorizationDecision negativeAuthorizationDecision() {
    return new AuthorizationDecision(false);
  }
}