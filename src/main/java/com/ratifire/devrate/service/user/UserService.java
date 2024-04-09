package com.ratifire.devrate.service.user;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for performing operations related to {@link User} entities.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final DataMapper<UserDto, User> userMapper;

  /**
   * Retrieves user by user ID.
   *
   * @param id the ID of the user
   * @return the user as a DTO
   * @throws UserNotFoundException if the user does not exist
   */
  public UserDto findById(long id) {
    return userRepository.findById(id).map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException("The user could not be found with id \""
            + id + "\""));
  }

  /**
   * Creates user.
   *
   * @param userDto the user as a DTO
   * @return the created user
   */
  public User create(UserDto userDto) {
    User user = userMapper.toEntity(userDto);
    return userRepository.save(user);
  }

  /**
   * Updates user.
   *
   * @param userDto the updated user as a DTO
   * @return the updated user as a DTO
   * @throws UserNotFoundException if the user does not exist
   */
  public UserDto update(UserDto userDto) {
    long userId = userDto.getId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("The user could not be found with id \""
            + userId + "\""));

    userMapper.updateEntity(userDto, user);

    return userMapper.toDto(userRepository.save(user));
  }

  /**
   * Deletes user by ID.
   *
   * @param userId the ID of the user
   * @throws UserNotFoundException if the user does not exist
   */
  public void delete(long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("The user could not be found with id \""
            + userId + "\""));

    userRepository.delete(user);
  }
}
