package com.ratifire.devrate.service.user;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the UserService class.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private DataMapper dataMapper;

  @Mock
  private UserRepository userRepository;

  private final long userId = 1L;
  private User testUser;
  private UserDto testUserDto;
  private EmploymentRecordDto employmentRecordDto;
  private EmploymentRecord employmentRecord;
  private final  byte[] picture = new byte[] {4, 5, 6};


  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void init() {
    testUser = User.builder()
        .id(userId)
        .employmentRecords(new ArrayList<>())
        .build();

    testUserDto = UserDto.builder()
        .id(userId)
        .build();

    employmentRecordDto = EmploymentRecordDto.builder()
        .id(1L)
        .startDate(LocalDate.ofEpochDay(2023 - 01 - 01))
        .endDate(LocalDate.ofEpochDay(2022 - 01 - 01))
        .position("Java Developer")
        .companyName("New Company")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("1", "2", "3"))
        .build();

    employmentRecord = EmploymentRecord.builder()
        .id(1L)
        .startDate(LocalDate.of(2023, 1, 1))
        .endDate(LocalDate.of(2023, 11, 22))
        .position("Java Developer")
        .companyName("Example Company 4")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("Собирал одуванчики", "Hello World", "3"))
        .build();
  }

  @Test
  void findUserById_UserExists_ReturnsUserDto() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toDto(any(User.class))).thenReturn(testUserDto);

    UserDto foundUser = userService.findById(userId);

    assertEquals(foundUser, testUserDto);
  }

  @Test
  void findUserById_UserNotFound_ThrowsUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
  }

  @Test
  void whenCreate_Successful_ReturnCreatedUser() {
    when(dataMapper.toEntity(any(UserDto.class))).thenReturn(testUser);
    when(userRepository.save(any())).thenReturn(testUser);

    User createdUser = userService.create(testUserDto);

    assertEquals(testUserDto.getId(), createdUser.getId());
    verify(userRepository).save(testUser);
    verify(dataMapper).toEntity(testUserDto);
  }

  @Test
  void whenUpdate_UserNotFound_ThrowUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.update(testUserDto));
  }

  @Test
  void whenUpdate_Successful_ReturnCreatedUserDto() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.updateEntity(any(UserDto.class), any(User.class))).thenReturn(testUser);
    when(userRepository.save(any())).thenReturn(testUser);
    when(dataMapper.toDto(any(User.class))).thenReturn(testUserDto);

    UserDto updatedUserDto = userService.update(testUserDto);

    assertEquals(testUserDto.getId(), updatedUserDto.getId());
    verify(userRepository).findById(userId);
    verify(userRepository).save(testUser);
    verify(dataMapper).updateEntity(testUserDto, testUser);
    verify(dataMapper).toDto(testUser);
  }

  @Test
  void whenUpdate_Successful_ReturnCreatedUser() {
    when(userRepository.save(any())).thenReturn(testUser);

    User updatedUser = userService.updateUser(testUser);

    assertEquals(userId, updatedUser.getId());
    verify(userRepository, times(1)).save(testUser);
  }

  @Test
  void whenDelete_UserNotFound_ThrowUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
  }

  @Test
  void delete_UserExists_DeletesUser() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

    userService.delete(userId);

    verify(userRepository, times(1)).delete(testUser);
  }

  @Test
  void createEmploymentRecord_Successful_ReturnCreatedEmploymentRecordDto() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toEntity(any(EmploymentRecordDto.class))).thenReturn(
        employmentRecord);
    when(dataMapper.toDto(any(EmploymentRecord.class))).thenReturn(
        employmentRecordDto);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    EmploymentRecordDto result = userService.createEmploymentRecord(employmentRecordDto, userId);

    assertEquals(employmentRecordDto, result);
  }

  @Test
  void getEmploymentRecordsByUserId_UserExists_ReturnsListOfEmploymentRecordDto() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.getEmploymentRecordsByUserId(userId));
  }

  @Test
  public void testGetUserPictureExists() {
    when(userRepository.findPictureByUserId(userId)).thenReturn(picture);

    byte[] actualPicture = userService.getUserPicture(userId);

    assertNotNull(actualPicture);
    assertArrayEquals(picture, actualPicture);
  }

  @Test
  public void testAddUserPictureNewPicture() {
    User user = new User();
    user.setId(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    byte[] actualPicture = userService.addUserPicture(userId, picture);

    assertArrayEquals(picture, actualPicture);
  }

  @Test
  public void testDeleteUserPicture() {
    User user = new User();
    user.setId(userId);
    user.setPicture(picture);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    userService.deleteUserPicture(userId);

    assertNull(user.getPicture());

    verify(userRepository).save(user);
  }
}