package com.ratifire.devrate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service implementation for retrieving user details.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserSecurityService userSecurityService;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userSecurityService.findByEmail(email);
  }
}
