package com.ratifire.devrate.service.user;

import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.entity.EmploymentRecord;
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
  private final DataMapper<ContactDto, Contact> contactMapper;
  private final DataMapper<EmploymentRecordDto, EmploymentRecord> employmentRecordMapper;

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
  public List<ContactDto> saveContacts(long userId,
      List<ContactDto> contactDtos) {
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