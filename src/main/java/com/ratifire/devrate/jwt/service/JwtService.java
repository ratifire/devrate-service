package com.ratifire.devrate.jwt.service;

import com.ratifire.devrate.jwt.exception.InvalidRefreshTokenException;
import com.ratifire.devrate.jwt.exception.RefreshTokenNotFoundException;
import com.ratifire.devrate.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * test.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

  @Value("${jwt.access.token.secret.key}")
  private String secretAccessKey;
  @Value("${jwt.refresh.token.secret.key}")
  private String secretRefreshKey;
  @Value("${jwt.access.token.expiration.time}")
  private long expirationAccessTokenTime;
  @Value("${jwt.refresh.token.expiration.time}")
  private long expirationRefreshTokenTime;

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String TOKEN_PREFIX = "Bearer ";

  private final UserDetailsServiceImpl userDetailsService;

  /**
   * test.
   */
  public String extractAccessTokenFromRequest(HttpServletRequest request) {
    final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
    if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
      return authHeader.substring(TOKEN_PREFIX.length());
    }
    return null;
  }

  /**
   * test.
   */
  public String extractRefreshTokenFromCookies(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("Refresh-Token".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    throw new RefreshTokenNotFoundException("Refresh Token not found in cookies.");
  }

  /**
   * test.
   */
  public boolean isAccessTokenValid(String jwt, String userEmail) {
    if (userEmail == null) {
      return false;
    }
    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
    return validateToken(jwt, userDetails);
  }

  /**
   * test.
   */
  public boolean validateToken(String token, UserDetails userDetails) {
    final Claims claims = extractClaimsFromAccessToken(token);
    final String username = claims.getSubject();
    final Date expiration = claims.getExpiration();
    return username.equals(userDetails.getUsername()) && expiration.after(new Date());
  }

  /**
   * test.
   */
  public boolean validateRefreshToken(String refreshToken) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getRefreshKey())
          .build()
          .parseClaimsJws(refreshToken);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  /**
   * test.
   */
  public String extractUsernameFromAccessToken(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * test.
   */
  public String extractUsernameFromRefreshToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getRefreshKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }

  /**
   * test.
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractClaimsFromAccessToken(token);
    return claimsResolver.apply(claims);
  }

  /**
   * test.
   */
  private Claims extractClaimsFromAccessToken(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getAccessKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * test.
   */
  private Key getAccessKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretAccessKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * test.
   */
  private Key getRefreshKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretRefreshKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * test.
   */
  public String generateAccessToken(UserDetails userDetails) {
    return generateAccessToken(new HashMap<>(), userDetails);
  }

  /**
   * test.
   */
  public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    Instant now = Instant.now();
    Instant expirationInstant = now.plus(expirationAccessTokenTime, ChronoUnit.MILLIS);
    Date expirationDate = Date.from(expirationInstant);

    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(Date.from(now))
        .setExpiration(expirationDate)
        .signWith(getAccessKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * test.
   */
  public String generateRefreshToken(UserDetails userDetails) {
    Instant now = Instant.now();
    Instant expirationInstant = now.plus(expirationRefreshTokenTime, ChronoUnit.MILLIS);
    Date expirationDate = Date.from(expirationInstant);
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(Date.from(Instant.now()))
        .setExpiration(expirationDate)
        .signWith(getRefreshKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * test.
   */
  public String generateAccessTokenFromRefreshToken(String refreshToken) {
    if (!validateRefreshToken(refreshToken)) {
      throw new InvalidRefreshTokenException("The provided Refresh Token is invalid.");
    }
    String username = extractUsernameFromRefreshToken(refreshToken);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return generateAccessToken(userDetails);
  }
}