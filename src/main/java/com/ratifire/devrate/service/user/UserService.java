package com.ratifire.devrate.service.user;

import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.LanguageProficiency;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.LanguageProficiencyAlreadyExistException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserRepository;
import java.util.List;
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
  private final DataMapper<LanguageProficiencyDto, LanguageProficiency> languageProficiencyMapper;

  /**
   * Retrieves a user by ID.
   *
   * @param id the ID of the user to retrieve
   * @return the user DTO
   * @throws UserNotFoundException if the user with the specified ID is not found
   */
  public UserDto findById(long id) {
    return userMapper.toDto(findUserById(id));
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
    User user = findUserById(userDto.getId());
    userMapper.updateEntity(userDto, user);
    return userMapper.toDto(userRepository.save(user));
  }

  /**
   * Updates an existing user entity.
   *
   * @param user the updated user entity
   * @return the updated user entity
   */
  public User updateUser(User user) {
    return userRepository.save(user);
  }

  /**
   * Deletes user by ID.
   *
   * @param userId the ID of the user
   * @throws UserNotFoundException if the user does not exist
   */
  public void delete(long userId) {
    User user = findUserById(userId);
    userRepository.delete(user);
  }

  /**
   * Retrieves all language proficiencies associated with the user.
   *
   * @param userId the ID of the user to associate the language proficiencies with
   * @return A list of LanguageProficiencyDto objects.
   */
  public List<LanguageProficiencyDto> findAllLanguageProficienciesByUserId(long userId) {
    User user = findUserById(userId);
    return languageProficiencyMapper.toDto(user.getLanguageProficiencies());
  }

  /**
   * Creates a new language proficiency for a user identified by userId.
   *
   * @param userId                 the ID of the user to whom the language proficiency belongs
   * @param languageProficiencyDto the language proficiency information to create
   * @return the created LanguageProficiencyDto
   */
  public LanguageProficiencyDto createLanguageProficiency(long userId,
      LanguageProficiencyDto languageProficiencyDto) {
    User user = findUserById(userId);

    if (user.getLanguageProficiencies().stream()
        .anyMatch(languageProficiency -> languageProficiency.getName()
            .equals(languageProficiencyDto.getName()))) {
      throw new LanguageProficiencyAlreadyExistException("Language proficiency already exists");
    }

    LanguageProficiency languageProficiency = languageProficiencyMapper.toEntity(
        languageProficiencyDto);
    user.getLanguageProficiencies().add(languageProficiency);
    userRepository.save(user);
    return languageProficiencyMapper.toDto(languageProficiency);
  }

  /**
   * Retrieves a user entity by ID.
   *
   * @param id the ID of the user to retrieve
   * @return the user entity
   * @throws UserNotFoundException if the user with the specified ID is not found
   */
  private User findUserById(long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("The user not found with id " + id));
  }

}
