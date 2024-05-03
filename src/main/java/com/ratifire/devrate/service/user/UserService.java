package com.ratifire.devrate.service.user;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.entity.LanguageProficiency;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserRepository;
import java.util.List;
import java.util.Optional;
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
  private final DataMapper<AchievementDto, Achievement> achievementMapper;
  private final DataMapper<ContactDto, Contact> contactMapper;
  private final DataMapper<EmploymentRecordDto, EmploymentRecord> employmentRecordMapper;
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
   * Saves new language proficiencies for a user identified by userId.
   *
   * @param userId                  the ID of the user to whom the language proficiency belongs
   * @param languageProficiencyDtos the language proficiency information to save
   * @return the list of saved LanguageProficiencyDto objects
   */
  public List<LanguageProficiencyDto> saveLanguageProficiencies(long userId,
      List<LanguageProficiencyDto> languageProficiencyDtos) {
    User user = findUserById(userId);
    List<LanguageProficiency> existingProficiencies = user.getLanguageProficiencies();

    existingProficiencies.removeIf(proficiency -> languageProficiencyDtos.stream()
        .noneMatch(proficiencyDto -> proficiencyDto.getName().equals(proficiency.getName())));

    for (LanguageProficiencyDto languageProficiencyDto : languageProficiencyDtos) {
      Optional<LanguageProficiency> languageProficiency = existingProficiencies.stream()
          .filter(proficiency -> proficiency.getName().equals(languageProficiencyDto.getName()))
          .findFirst();

      if (languageProficiency.isPresent()) {
        languageProficiencyMapper.updateEntity(languageProficiencyDto, languageProficiency.get());
      } else {
        existingProficiencies.add(languageProficiencyMapper.toEntity(languageProficiencyDto));
      }
    }
    userRepository.save(user);
    return languageProficiencyMapper.toDto(user.getLanguageProficiencies());
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

  /**
   * Retrieves EmploymentRecord (work experience) information by user ID.
   *
   * @param userId the ID of the user
   * @return the user's work experience as a DTO
   */
  public List<EmploymentRecordDto> getEmploymentRecordsByUserId(long userId) {
    User user = findUserById(userId);
    return employmentRecordMapper.toDto(user.getEmploymentRecords());
  }

  /**
   * Creates EmploymentRecord information.
   *
   * @param employmentRecordDto the user's EmploymentRecord information as a DTO
   * @return the created user work experience information as a DTO
   */
  public EmploymentRecordDto createEmploymentRecord(EmploymentRecordDto employmentRecordDto,
      long userId) {
    User user = findUserById(userId);
    EmploymentRecord employmentRecord = employmentRecordMapper.toEntity(employmentRecordDto);
    user.getEmploymentRecords().add(employmentRecord);
    updateUser(user);
    return employmentRecordMapper.toDto(employmentRecord);
  }

  /**
   * Retrieves the picture associated with a user by their user ID.
   *
   * @param userId the ID of the user whose picture is to be retrieved
   * @return the user's picture as a byte array, or null if no picture is present
   */
  public byte[] getUserPicture(long userId) {
    return userRepository.findPictureByUserId(userId);
  }

  /**
   * Adds or updates a user's picture by user ID. If the user already has a picture, it is replaced.
   *
   * @param userId the ID of the user whose picture is to be added or updated
   * @param userPicture the picture data as a byte array
   */
  public void addUserPicture(long userId, byte[] userPicture) {
    User user = findUserById(userId);
    user.setPicture(userPicture);
    updateUser(user);
  }

  /**
   * Removes a user's picture by user ID.
   *
   * @param userId the ID of the user whose picture is to be removed
   */
  public void deleteUserPicture(long userId) {
    User user = findUserById(userId);
    user.setPicture(null);
    updateUser(user);
  }

  /**
   * Retrieves a list of achievements for a specific user by their ID.
   *
   * @param userId The ID of the user whose achievements are to be retrieved.
   * @return A list of AchievementDto objects representing the achievements of the user.
   */
  public List<AchievementDto> getAchievementsByUserId(long userId) {
    User user = findUserById(userId);
    return achievementMapper.toDto(user.getAchievements());
  }

  /**
   * Creates a new achievement for a specific user.
   *
   * @param userId         The ID of the user for whom the achievement is to be created.
   * @param achievementDto The AchievementDto object containing details of the achievement to be
   *                       created.
   * @return The AchievementDto object representing the created achievement.
   */
  public AchievementDto createAchievement(long userId, AchievementDto achievementDto) {
    User user = findUserById(userId);
    Achievement achievement = achievementMapper.toEntity(achievementDto);
    user.getAchievements().add(achievement);
    updateUser(user);
    return achievementMapper.toDto(achievement);
  }

  /**
   * Retrieves all contacts associated with the user.
   *
   * @param userId the ID of the user to associate the contacts with
   * @return A list of ContactDto objects.
   */
  public List<ContactDto> findAllContactsByUserId(long userId) {
    User user = findUserById(userId);
    return contactMapper.toDto(user.getContacts());
  }

  /**
   * Saves contacts for a user identified by userId.
   *
   * @param userId      the ID of the user to whom the contacts belongs
   * @param contactDtos the contact information to save
   * @return the list of saved ContactDto objects
   */
  public List<ContactDto> saveContacts(long userId, List<ContactDto> contactDtos) {
    User user = findUserById(userId);
    List<Contact> existingContacts = user.getContacts();

    existingContacts.removeIf(contact -> contactDtos.stream()
        .noneMatch(contactDto -> contactDto.getType().equals(contact.getType())));

    for (ContactDto contactDto : contactDtos) {
      Optional<Contact> contact = existingContacts.stream()
          .filter(c -> c.getType().equals(contactDto.getType()))
          .findFirst();

      if (contact.isPresent()) {
        contactMapper.updateEntity(contactDto, contact.get());
      } else {
        existingContacts.add(contactMapper.toEntity(contactDto));
      }
    }
    userRepository.save(user);
    return contactMapper.toDto(user.getContacts());
  }

}
