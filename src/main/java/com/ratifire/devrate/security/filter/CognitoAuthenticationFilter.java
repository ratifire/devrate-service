package com.ratifire.devrate.security.filter;

import static com.ratifire.devrate.security.model.CognitoTypeToken.ACCESS_TOKEN;
import static com.ratifire.devrate.security.model.CognitoTypeToken.ID_TOKEN;

import com.ratifire.devrate.security.exception.TokenValidationException;
import com.ratifire.devrate.security.service.CognitoTokenValidationService;
import com.ratifire.devrate.security.util.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
public class CognitoAuthenticationFilter extends OncePerRequestFilter {

  private final CognitoTokenValidationService cognitoTokenValidationService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = TokenUtil.extractAccessTokenFromRequest(request);
    String idToken = TokenUtil.extractIdTokenFromRequest(request);

    if (accessToken == null || idToken == null) {
      filterChain.doFilter(request, response);
      return;
    }

    if (cognitoTokenValidationService.validateToken(accessToken, ACCESS_TOKEN)
        && cognitoTokenValidationService.validateToken(idToken, ID_TOKEN)) {

      long userId = TokenUtil.getUserIdFromIdToken(idToken);
      List<SimpleGrantedAuthority> authorities = TokenUtil.getAuthoritiesFromIdToken(idToken);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userId, null, authorities);
      SecurityContextHolder.getContext().setAuthentication(authentication);

    } else {
      throw new TokenValidationException("Token validation failed");
    }

    filterChain.doFilter(request, response);
  }
}