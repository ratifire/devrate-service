package com.ratifire.devrate.mapper;


import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Component responsible for mapping and encoding passwords.
 */
@Component
@RequiredArgsConstructor
@Named("PasswordEncoderMapper")
public class PasswordEncoderMapper {

  private final PasswordEncoder passwordEncoder;

  @Named("encode")
  public String encode(String value) {
    return passwordEncoder.encode(value);
  }
}
