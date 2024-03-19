package com.ratifire.devrate.service.userinfo;

import com.ratifire.devrate.dto.UserPersonalInfoDto;
import com.ratifire.devrate.entity.UserPersonalInfo;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.exception.UserPersonalInfoNotFoundException;
import com.ratifire.devrate.mapper.UserPersonalInfoMapper;
import com.ratifire.devrate.repository.UserPersonalInfoRepository;
import com.ratifire.devrate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for performing operations related to {@link UserPersonalInfo} entities.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPersonalInfoService {

  private final UserPersonalInfoRepository userPersonalInfoRepository;
  private final UserPersonalInfoMapper userPersonalInfoMapper;
  private final UserService userService;

  /**
   * Retrieves user personal information by user ID.
   *
   * @param userId the ID of the user
   * @return the user's personal information as a DTO
   * @throws UserPersonalInfoNotFoundException if user personal information is not found
   */
  public UserPersonalInfoDto getById(long userId) {
    return userPersonalInfoRepository.findByUserId(userId).map(userPersonalInfoMapper::toDto)
        .orElseThrow(() -> new UserPersonalInfoNotFoundException("The user's personal information "
            + "could not be found with the user id \"" + userId + "\""));
  }

  /**
   * Creates user personal information.
   *
   * @param userId              the ID of the user
   * @param userPersonalInfoDto the user's personal information as a DTO
   * @return the created user personal information as a DTO
   * @throws UserNotFoundException if the user does not exist
   * @throws UserPersonalInfoNotFoundException if the user personal info does not exist by user id
   */
  @Transactional
  public UserPersonalInfoDto create(long userId, UserPersonalInfoDto userPersonalInfoDto) {
    if (!userService.isUserExistsById(userId)) {
      throw new UserNotFoundException("The user could not be found with the id \"" + userId + "\"");
    }

    if (!userPersonalInfoRepository.existsByUserId(userId)) {
      throw new UserPersonalInfoNotFoundException("The user's personal information could not be "
          + "found with the user id \"" + userId + "\"");
    }

    UserPersonalInfo userPersonalInfo = userPersonalInfoMapper.toEntity(userPersonalInfoDto);
    userPersonalInfo.setUserId(userId);

    return userPersonalInfoMapper.toDto(userPersonalInfoRepository.save(userPersonalInfo));
  }

  /**
   * Updates user personal information.
   *
   * @param userId              the ID of the user
   * @param userPersonalInfoDto the updated user's personal information as a DTO
   * @return the updated user personal information as a DTO
   * @throws UserNotFoundException if the user does not exist
   * @throws UserPersonalInfoNotFoundException if the user personal info does not exist by user id
   */
  @Transactional
  public UserPersonalInfoDto update(long userId, UserPersonalInfoDto userPersonalInfoDto) {
    if (!userService.isUserExistsById(userId)) {
      throw new UserNotFoundException("The user could not be found with the id \"" + userId + "\"");
    }

    UserPersonalInfo userPersonalInfo = userPersonalInfoRepository.findByUserId(userId)
        .orElseThrow(() -> new UserPersonalInfoNotFoundException("The user's personal information "
            + "could not be found with the user id \"" + userId + "\""));

    userPersonalInfoMapper.updateEntityFromDto(userPersonalInfoDto, userPersonalInfo);

    return userPersonalInfoMapper.toDto(userPersonalInfoRepository.save(userPersonalInfo));
  }

  /**
   * Deletes user personal information by user ID.
   *
   * @param userId the ID of the user whose personal information is to be deleted
   * @throws UserNotFoundException             if the user does not exist
   * @throws UserPersonalInfoNotFoundException if user personal information is not found
   */
  @Transactional
  public void delete(long userId) {
    if (!userService.isUserExistsById(userId)) {
      throw new UserNotFoundException("The user could not be found with the id \"" + userId + "\"");
    }

    UserPersonalInfo userPersonalInfo = userPersonalInfoRepository.findByUserId(userId)
        .orElseThrow(() -> new UserPersonalInfoNotFoundException("The user's personal information "
            + "could not be found with the user id \"" + userId + "\""));

    userPersonalInfoRepository.delete(userPersonalInfo);
  }
}
