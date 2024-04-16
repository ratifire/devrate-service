package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.LoginDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.UserSecurityNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user security entities in the system. This service provides methods
 * for creating, retrieving, updating, and deleting user security entities.
 */
@Service
@RequiredArgsConstructor
public class UserSecurityService {

  private final UserSecurityRepository userSecurityRepository;
  private final AuthenticationManager authenticationManager;
  private final DataMapper<UserDto, User> userMapper;

  /**
   * Checks if a user security with the given email exists in the system.
   *
   * @param email The email address of the user security to check.
   * @return True if a user security with the given email exists, false otherwise.
   */
  public boolean isExistByEmail(String email) {
    return userSecurityRepository.existsByEmail(email);
  }

  /**
   * Saves the user security entity.
   *
   * @param userSecurity The user security entity to save.
   * @return The saved user security entity.
   */
  public UserSecurity save(UserSecurity userSecurity) {
    return userSecurityRepository.save(userSecurity);
  }

  /**
   * Get the user security entity by id.
   *
   * @param id The user security id.
   * @return The user security entity that has been found.
   */
  public UserSecurity getById(long id) {
    return userSecurityRepository.findById(id)
        .orElseThrow(() -> new UserSecurityNotFoundException("The user security cannot be "
            + "found."));
  }

  /**
   * Retrieves user security information by user ID.
   *
   * @param userId The ID of the user.
   * @return The UserSecurity object associated with the provided user ID.
   * @throws UserSecurityNotFoundException If the user security information cannot be found for the
   *                                       given user ID.
   */
  public UserSecurity getByUserId(long userId) {
    return userSecurityRepository.findByUserId(userId)
        .orElseThrow(() -> new UserSecurityNotFoundException("The user security cannot be "
            + "found."));
  }

  /**
   * Finds a user security entity by email.
   *
   * @param email The email address of the user security to find.
   * @return The user security entity if found, otherwise throws UsernameNotFoundException.
   * @throws UsernameNotFoundException if the user security with the given email is not found.
   */
  public UserSecurity findByEmail(String email) throws UsernameNotFoundException {
    return userSecurityRepository.findByEmail(email).orElseThrow();
  }

  /**
   * Authenticates a user based on the provided login credentials.
   *
   * @param loginDto The data transfer object containing the user's login information.
   * @return A UserDto object representing the authenticated user.
   */
  public UserDto authenticate(LoginDto loginDto) {
    String login = loginDto.getEmail();
    String password = loginDto.getPassword();

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(login, password));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    return userMapper.toDto(findByEmail(login).getUser());
  }
}
