package com.ratifire.devrate.mapper.encoder;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Component responsible for mapping and encoding passwords.
 */
@Component
@RequiredArgsConstructor
public class PasswordEncoderMapper {

  private final PasswordEncoder passwordEncoder;

  @EncodedMapping
  public String encode(String value) {
    return passwordEncoder.encode(value);
  }
}
