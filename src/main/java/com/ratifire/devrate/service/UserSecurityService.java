package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.repository.UserSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service layer component responsible for managing user security-related operations.
 */
@Service
@RequiredArgsConstructor
public class UserSecurityService {

  private final UserSecurityRepository userSecurityRepository;

  /**
   * Saves user security data.
   *
   * @param userSecurity The user security data to be saved.
   * @return True if the user security data is successfully saved, false otherwise.
   */
  public UserSecurity save(UserSecurity userSecurity) {
    return userSecurityRepository.save(userSecurity);
  }
}
