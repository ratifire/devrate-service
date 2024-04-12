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

  /**
   * Retrieves all language proficiencies associated with the user.
   *
   * @param userId the ID of the user to associate the language proficiencies with
   * @return A list of LanguageProficiencyDto objects.
   */
  public List<LanguageProficiencyDto> findAllLanguageProficienciesByUserId(long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("The user could not be found with id \""
            + userId + "\""));

    return languageProficiencyMapper.toDto(user.getLanguageProficiency());

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
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("The user could not be found with id \""
            + userId + "\""));

    if (user.getLanguageProficiency().stream()
        .anyMatch(languageProficiency -> languageProficiency.getName()
            .equals(languageProficiencyDto.getName()))) {
      throw new LanguageProficiencyAlreadyExistException("Language proficiency already exists");
    }

    LanguageProficiency languageProficiency = languageProficiencyMapper.toEntity(
        languageProficiencyDto);
    user.getLanguageProficiency().add(languageProficiency);
    userRepository.save(user);
    return languageProficiencyMapper.toDto(languageProficiency);
  }

}
