package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import java.util.List;
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

  private final UserService userService;
  private final UserSecurityService userSecurityService;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userService.findUserByEmail(email);
    UserSecurity userSecurity = userSecurityService.findByUserId(user.getId());
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        userSecurity.getPassword(),
        List.of(userSecurity.getRole()));
  }
}
