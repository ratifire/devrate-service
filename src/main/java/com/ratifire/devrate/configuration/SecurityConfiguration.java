package com.ratifire.devrate.configuration;

import com.ratifire.devrate.service.authorization.ResourceOwnerVerifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration class for security settings.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final UserDetailsService userDetailsService;

  private static final String[] WHITELIST = {
      // -- health-check
      "/actuator/**",
      // -- authentication
      "/auth/**",
      // -- swagger ui
      "/swagger/**",
      "/swagger-ui/**",
      "/swagger-config.yaml",
      "/v3/api-docs/**",
      // -- static resources
      "/data/user/countries.json",
      // -- zoom webhook
      "/zoom/webhook/events"
  };

  @Bean
  public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configures security filters.
   *
   * @param http the HttpSecurity object to configure
   * @return the SecurityFilterChain object
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests((authorize) -> authorize
            .requestMatchers(WHITELIST).permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .exceptionHandling(
            e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .build();
  }

  /**
   * Configures a CORS (Cross-Origin Resource Sharing) setup to allow all origins, methods etc.
   *
   * @return Configured CorsConfigurationSource for cross-origin requests.
   */
  @Bean
  @ConditionalOnProperty(prefix = "cors", name = "enabled", havingValue = "false")
  public CorsConfigurationSource corsConfigurationSource(
      @Value("${cors.allowed.origins}") String allowedOrigins) {
    List<String> origins = Arrays.asList(allowedOrigins.split(","));
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(origins);
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }


  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
  }

  @Bean
  public Map<String, ResourceOwnerVerifier> resourceTypeToOwnerVerifier() {
    return new HashMap<>();
  }

  /**
   * Configures a {@link org.springframework.boot.web.servlet.ServletContextInitializer} bean to set
   * up session cookie settings for the application.
   *
   * @param domain The domain value for the session cookie, injected from the application
   *               properties.
   * @return A {@link ServletContextInitializer} to configure session cookie settings.
   */

  @Bean
  public ServletContextInitializer servletContextInitializer(
      @Value("${server.servlet.session.cookie.domain}") String domain) {
    return servletContext -> {
      servletContext.getSessionCookieConfig().setDomain(domain);
      servletContext.getSessionCookieConfig().setPath("/");
    };
  }
}