package com.ratifire.devrate.security.filter;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_AUTHENTICATION_ERROR;
import static com.ratifire.devrate.security.model.enums.AuthenticationError.AUTH_TOKEN_EXPIRED;
import static com.ratifire.devrate.security.model.enums.AuthenticationError.UNAUTHORIZED;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.ACCESS_TOKEN;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.ID_TOKEN;

import com.ratifire.devrate.security.exception.AuthTokenExpiredException;
import com.ratifire.devrate.security.exception.AuthenticationException;
import com.ratifire.devrate.security.service.CognitoTokenValidationService;
import com.ratifire.devrate.security.util.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter responsible for authenticating requests using Cognito tokens.
 */
@Component
@RequiredArgsConstructor
@Profile("!local")
public class CognitoAuthenticationFilter extends OncePerRequestFilter {

  private final CognitoTokenValidationService validationService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    Set<String> excludedPaths = Set.of(
        "/auth/refresh-token",
        "/auth/{provider}/login",
        "/auth/callback"
    );
    return excludedPaths.contains(request.getServletPath());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = TokenUtil.extractAccessTokenFromRequest(request);
    String idToken = TokenUtil.extractIdTokenFromRequest(request);

    if (accessToken == null || idToken == null) {
      request.setAttribute(ATTRIBUTE_AUTHENTICATION_ERROR, UNAUTHORIZED);
      filterChain.doFilter(request, response);
      return;
    }

    try {

      if (validationService.validateToken(accessToken, ACCESS_TOKEN)
          && validationService.validateToken(idToken, ID_TOKEN)) {
        setUpAuthenticationContext(idToken);
      }

      filterChain.doFilter(request, response);

    } catch (AuthTokenExpiredException e) {
      request.setAttribute(ATTRIBUTE_AUTHENTICATION_ERROR, AUTH_TOKEN_EXPIRED);
      throw new AuthTokenExpiredException(
          "Authentication process was failed. Token has been expired");

    } catch (Exception e) {
      request.setAttribute(ATTRIBUTE_AUTHENTICATION_ERROR, UNAUTHORIZED);
      throw new AuthenticationException("Authentication process was failed");
    }
  }

  private void setUpAuthenticationContext(String idToken) {
    long userId = TokenUtil.getUserIdFromIdToken(idToken);
    List<SimpleGrantedAuthority> authorities = TokenUtil.getAuthoritiesFromIdToken(idToken);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userId, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
