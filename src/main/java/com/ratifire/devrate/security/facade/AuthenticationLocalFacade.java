package com.ratifire.devrate.security.facade;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.HEADER_AUTHORIZATION;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.HEADER_ID_TOKEN;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.security.exception.AuthenticationException;
import com.ratifire.devrate.security.exception.UserAlreadyExistsException;
import com.ratifire.devrate.security.exception.UserRegistrationException;
import com.ratifire.devrate.security.helper.RefreshTokenCookieHelper;
import com.ratifire.devrate.security.model.dto.ConfirmRegistrationDto;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.model.dto.OauthAuthorizationDto;
import com.ratifire.devrate.security.model.dto.PasswordResetDto;
import com.ratifire.devrate.security.model.dto.ResendConfirmCodeDto;
import com.ratifire.devrate.security.model.dto.UserRegistrationDto;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Local implementation facade class.
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Profile("local")
public class AuthenticationLocalFacade implements AuthenticationFacade {

  private static final String MSG_UNSUPPORTED_OPERATION = "Unsupported operation locally.";
  private final UserService userService;
  private final DataMapper<UserDto, User> userMapper;
  private final RefreshTokenCookieHelper refreshTokenCookieHelper;

  @Override
  public UserDto login(LoginDto loginDto, HttpServletResponse response) {
    String email = loginDto.getEmail().toLowerCase();
    try {
      User user = userService.findByEmail(email);
      String encodedUserId =
          Base64.getEncoder().encodeToString(String.valueOf(user.getId()).getBytes());
      TokenUtil.setAuthTokensToHeaders(response, encodedUserId, encodedUserId);

      return userMapper.toDto(user);

    } catch (Exception e) {
      log.error("Authentication process was failed for email {}: {}", email, e.getMessage());
      throw new AuthenticationException("Authentication process was failed.");
    }
  }

  @Override
  public void redirectToLinkedIn(HttpServletResponse response) {
    throw new UnsupportedOperationException(MSG_UNSUPPORTED_OPERATION);
  }

  @Override
  public void redirectToGoogle(HttpServletResponse response) {
    throw new UnsupportedOperationException(MSG_UNSUPPORTED_OPERATION);
  }

  @Override
  public UserDto handleOauthAuthorization(HttpServletResponse response,
      OauthAuthorizationDto request) {
    throw new UnsupportedOperationException(MSG_UNSUPPORTED_OPERATION);
  }

  @Override
  public String logout(HttpServletRequest request, HttpServletResponse response) {
    refreshTokenCookieHelper.deleteRefreshTokenFromCookie(response);
    return "Logout process was successfully completed.";
  }

  @Override
  public void registerUser(UserRegistrationDto userRegistrationDto) {
    String email = userRegistrationDto.getEmail().toLowerCase();
    String password = userRegistrationDto.getPassword();
    UserDto userDto = UserDto.builder()
        .firstName(userRegistrationDto.getFirstName())
        .lastName(userRegistrationDto.getLastName())
        .build();

    if (userService.existsByEmail(email)) {
      log.error("User with email {} already exists.", email);
      throw new UserAlreadyExistsException("User with email " + email + " already exists.");
    }

    try {
      userService.create(userDto, email, new BCryptPasswordEncoder().encode(password));
    } catch (Exception e) {
      log.error("Initiate registration process was failed for email {}: {}",
          userRegistrationDto.getEmail(), e.getMessage(), e);
      throw new UserRegistrationException("Initiate registration process was failed.");
    }
  }

  @Override
  public void resendRegistrationConfirmCode(ResendConfirmCodeDto resendConfirmCodeDto) {
    throw new UnsupportedOperationException(MSG_UNSUPPORTED_OPERATION);
  }

  @Override
  public long confirmRegistration(ConfirmRegistrationDto confirmationCodeDto) {
    User user = userService.findByEmail(confirmationCodeDto.getEmail());
    return user.getId();
  }

  @Override
  public void confirmResetPassword(PasswordResetDto passwordResetDto) {
    throw new UnsupportedOperationException(MSG_UNSUPPORTED_OPERATION);
  }

  @Override
  public void resetPassword(String email) {
    throw new UnsupportedOperationException(MSG_UNSUPPORTED_OPERATION);
  }

  @Override
  public void refreshAuthTokens(HttpServletRequest request, HttpServletResponse response) {
    throw new UnsupportedOperationException(MSG_UNSUPPORTED_OPERATION);
  }

  /**
   * Configuration class for local run security settings.
   */
  @Configuration
  @EnableWebSecurity
  @EnableMethodSecurity
  @RequiredArgsConstructor
  @Profile("local")
  public static class SecurityConfiguration {

    private final LocalAuthenticationFilter localAuthenticationFilter;

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
              .anyRequest().permitAll()
          )
          .addFilterBefore(localAuthenticationFilter,
              UsernamePasswordAuthenticationFilter.class)
          .build();
    }

    /**
     * Configures a {@link org.springframework.boot.web.servlet.ServletContextInitializer} bean to
     * set up session cookie settings for the application.
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
      configuration.setExposedHeaders(List.of(HEADER_AUTHORIZATION, HEADER_ID_TOKEN));
      configuration.setAllowCredentials(true);
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
    }

  }

  /**
   * Filter responsible for authenticating requests using encoded user ids to run locally.
   */
  @Component
  @RequiredArgsConstructor
  @Profile("local")
  public static class LocalAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
      return request.getServletPath().equals("/auth/signin");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
      String userId = request.getHeader(HEADER_ID_TOKEN);
      if (userId == null) {
        filterChain.doFilter(request, response);
        return;
      }
      setUpAuthenticationContext(Long.parseLong(new String(Base64.getDecoder().decode(userId))));
      filterChain.doFilter(request, response);
    }

    private void setUpAuthenticationContext(Long userId) {
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userId, null, List.of());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
  }
}
