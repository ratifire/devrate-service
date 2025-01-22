package com.ratifire.devrate.security.configuration;


import com.ratifire.devrate.security.configuration.handler.AuthenticationFailureHandlerEntryPoint;
import com.ratifire.devrate.security.filter.CognitoAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
@Profile("!local")
public class SecurityConfiguration {

  private final CognitoAuthenticationFilter cognitoAuthenticationFilter;
  private final AuthenticationFailureHandlerEntryPoint authenticationEntryPoint;

  private static final String[] WHITELIST = {
      // -- health-check
      "/actuator/**",
      // -- authentication
      "/auth/**",
      // -- sockJS
      "/chat/**",
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
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(WHITELIST).permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(cognitoAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(e -> e
            .authenticationEntryPoint(authenticationEntryPoint))
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
      @Value("${cors.allowed.origins}") List<String> allowedOrigins) {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Authorization", "ID-Token"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
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

  /**
   * Provides a PasswordEncoder bean that uses BCrypt hashing algorithm.
   */
  @Bean
  public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
