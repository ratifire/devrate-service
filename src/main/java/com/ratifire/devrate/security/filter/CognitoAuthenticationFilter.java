package com.ratifire.devrate.security.filter;

import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.ACCESS_TOKEN;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.ID_TOKEN;

import com.ratifire.devrate.security.configuration.properties.WhitelistPathProperties;
import com.ratifire.devrate.security.exception.AuthTokenExpiredException;
import com.ratifire.devrate.security.service.CognitoTokenValidationService;
import com.ratifire.devrate.security.util.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter responsible for authenticating requests using Cognito tokens.
 */
@Component
@RequiredArgsConstructor
@Profile("!local")
public class CognitoAuthenticationFilter extends OncePerRequestFilter {

  private static final int EXPIRED_AUTH_TOKEN_HTTP_STATUS = 498;
  private final CognitoTokenValidationService cognitoTokenValidationService;
  private final WhitelistPathProperties whitelistPathProperties;
  private final PathMatcher pathMatcher;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    if (StringUtils.isEmpty(path)) {
      return false;
    }
    return whitelistPathProperties.getWhitelistedPaths().stream()
        .anyMatch(pattern -> pathMatcher.match(pattern, path));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = TokenUtil.extractAccessTokenFromRequest(request);
    String idToken = TokenUtil.extractIdTokenFromRequest(request);

    if (accessToken == null || idToken == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      filterChain.doFilter(request, response);
      return;
    }

    try {

      if (cognitoTokenValidationService.validateToken(accessToken, ACCESS_TOKEN)
          && cognitoTokenValidationService.validateToken(idToken, ID_TOKEN)) {
        setUpAuthenticationContext(idToken);
      }

      filterChain.doFilter(request, response);

    } catch (AuthTokenExpiredException e) {
      response.setStatus(EXPIRED_AUTH_TOKEN_HTTP_STATUS);
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
