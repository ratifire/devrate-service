package com.ratifire.devrate.service.userinfo;

import com.ratifire.devrate.dto.UserInfoDto;
import com.ratifire.devrate.entity.UserInfo;
import com.ratifire.devrate.exception.UserInfoAlreadyExistsException;
import com.ratifire.devrate.exception.UserInfoNotFoundException;
import com.ratifire.devrate.mapper.UserInfoMapper;
import com.ratifire.devrate.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for performing operations related to {@link UserInfo} entities.
 */
@Service
@RequiredArgsConstructor
public class UserInfoService {

  private final UserInfoRepository userInfoRepository;
  private final UserInfoMapper userInfoMapper;

  /**
   * Retrieves user personal information by user ID.
   *
   * @param id the ID of the user
   * @return the user's personal information as a DTO
   * @throws UserInfoNotFoundException if user personal information is not found
   */
  public UserInfoDto findById(long id) {
    return userInfoRepository.findById(id).map(userInfoMapper::toDto)
        .orElseThrow(() -> new UserInfoNotFoundException("The user's personal information "
            + "could not be found with the user id \"" + id + "\""));
  }

  /**
   * Creates user personal information.
   *
   * @param userInfoDto the user's personal information as a DTO
   * @return the created user personal information as a DTO
   * @throws UserInfoAlreadyExistsException if the user personal info already exists
   */
  public UserInfoDto create(UserInfoDto userInfoDto) {
    long userId = userInfoDto.getUserId();
    if (userInfoRepository.existsById(userId)) {
      throw new UserInfoAlreadyExistsException("The user's personal information "
          + "with the user id \"" + userId + "\" already exists");
    }

    UserInfo userInfo = userInfoMapper.toEntity(userInfoDto);
    userInfo.setUserId(userId);

    return userInfoMapper.toDto(userInfoRepository.save(userInfo));
  }

  /**
   * Updates user personal information.
   *
   * @param userInfoDto the updated user's personal information as a DTO
   * @return the updated user personal information as a DTO
   * @throws UserInfoNotFoundException if the user personal info does not exist by user id
   */
  public UserInfoDto update(UserInfoDto userInfoDto) {
    long userId = userInfoDto.getUserId();
    UserInfo userInfo = userInfoRepository.findById(userId)
        .orElseThrow(() -> new UserInfoNotFoundException("The user's personal information "
            + "could not be found with the user id \"" + userId + "\""));

    userInfoMapper.updateEntityFromDto(userInfoDto, userInfo);

    return userInfoMapper.toDto(userInfoRepository.save(userInfo));
  }

  /**
   * Deletes user personal information by user ID.
   *
   * @param userId the ID of the user whose personal information is to be deleted
   * @throws UserInfoNotFoundException if user personal information is not found
   */
  public void delete(long userId) {
    UserInfo userInfo = userInfoRepository.findById(userId)
        .orElseThrow(() -> new UserInfoNotFoundException("The user's personal information "
            + "could not be found with the user id \"" + userId + "\""));

    userInfoRepository.delete(userInfo);
  }
}
