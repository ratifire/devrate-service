package com.ratifire.devrate.jwt.service;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.jwt.model.entity.RefreshToken;
import com.ratifire.devrate.jwt.repository.RefreshTokenRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * test.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  @Value("${jwt.refresh.token.expiration.time}")
  private int expirationTime;

  private final RefreshTokenRepository refreshTokenRepository;

  /**
   * test.
   */
  public void save(User user, String refreshToken) {
    RefreshToken token = RefreshToken.builder()
        .token(refreshToken)
        .user(user)
        .expiration(Instant.now().plus(10, ChronoUnit.DAYS))
        .createdAt(Instant.now())
        .active(true)
        .build();

    refreshTokenRepository.save(token);
  }

  /**
   * test.
   */
  public void update(String oldRefreshToken, String newRefreshToken) {
    RefreshToken token = refreshTokenRepository.findByToken(oldRefreshToken);

    if (token != null) {
      token.setToken(newRefreshToken);
      token.setExpiration(Instant.now().plus(expirationTime, ChronoUnit.DAYS));
      refreshTokenRepository.save(token);
    } else {
      throw new IllegalArgumentException("Refresh token not found.");
    }
  }

  /**
   * test.
   */
  public boolean validate(String token) {
    return refreshTokenRepository.existsByToken(token);
  }

  public void delete(String token) {
    refreshTokenRepository.deleteByToken(token);
  }
}