package com.ratifire.devrate.service.avatar.impl;

import com.ratifire.devrate.dto.AvatarDto;
import com.ratifire.devrate.entity.Avatar;
import com.ratifire.devrate.exception.MissingFileDataException;
import com.ratifire.devrate.exception.UserAvatarAlreadyExistException;
import com.ratifire.devrate.exception.UserAvatarNotFoundException;
import com.ratifire.devrate.repository.AvatarRepository;
import com.ratifire.devrate.service.avatar.AvatarService;
import com.ratifire.devrate.util.avatar.AvatarUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * A service for managing user avatars within a local environment, leveraging a local storage
 * mechanism. This service provides functionality to add, update, and remove user avatars, as well
 * as retrieve the path to a user's avatar. It's designed to work in a 'local' profile, typically
 * for development or testing purposes.
 */
@Service
@AllArgsConstructor
@Profile("local")
public class AvatarLocalService implements AvatarService {

  private final AvatarRepository avatarRepository;
  private final AvatarUtils avatarUtils;

  /**
   * Retrieves the avatar information for a user. If the avatar does not exist, it returns a default
   * avatar.
   *
   * @param userId the ID of the user whose avatar information is being retrieved.
   * @return AvatarDto containing the path to the user's avatar or the default avatar.
   * @throws UserAvatarNotFoundException if the avatar cannot be found in the repository.
   */
  @Override
  public AvatarDto get(long userId) {
    Optional<Avatar> avatar = avatarRepository.findByUserId(userId);
    return avatar
        .map(value -> new AvatarDto(value.getPath()))
        .orElseGet(() -> new AvatarDto(getBaseAvatar()));
  }

  /**
   * Adds a new avatar for a user. Validates the avatar file, generates a unique file name, and
   * constructs the image URL to be saved in the repository. If the user already has an avatar, an
   * exception is thrown.
   *
   * @param userId the ID of the user for whom the avatar is being added.
   * @param avatarFile the file of the avatar to add.
   * @return AvatarDto containing the URL of the added avatar.
   * @throws UserAvatarAlreadyExistException if the user already has an avatar.
   */
  @Override
  public AvatarDto add(long userId, MultipartFile avatarFile) {
    if (avatarRepository.existsByUserId(userId)) {
      throw new UserAvatarAlreadyExistException("The user already has an avatar");
    }
    avatarUtils.fileValidation(avatarFile);
    String uniqFileName = avatarUtils.generateUniqueFileName(avatarFile.getOriginalFilename());
    String imageUrl = getBasePath(avatarFile);
    Avatar avatar = Avatar.builder().fileName(uniqFileName).userId(userId).path(imageUrl).build();
    avatarRepository.save(avatar);
    return new AvatarDto(imageUrl);
  }

  /**
   * Updates an existing avatar for a user. Validates the new avatar file, generates a unique file
   * name, constructs the new image URL, and updates the avatar information in the repository.
   *
   * @param userId the ID of the user whose avatar is being updated.
   * @param avatarFile the new avatar file.
   * @return AvatarDto containing the URL of the updated avatar.
   * @throws UserAvatarNotFoundException if the avatar to update cannot be found.
   */
  @Override
  public AvatarDto update(long userId, MultipartFile avatarFile) {
    Avatar avatar =
        avatarRepository
            .findByUserId(userId)
            .orElseThrow(() -> new UserAvatarNotFoundException("Avatar to update was not found"));
    avatarUtils.fileValidation(avatarFile);
    String uniqueFileName = avatarUtils.generateUniqueFileName(avatarFile.getOriginalFilename());
    String imageUrl = getBasePath(avatarFile);
    avatar.setFileName(uniqueFileName);
    avatar.setPath(imageUrl);
    avatarRepository.save(avatar);
    return new AvatarDto(imageUrl);
  }

  /**
   * Removes a user's avatar from the repository. If the avatar does not exist, it throws an
   * exception.
   *
   * @param userId the ID of the user whose avatar is to be removed.
   * @throws UserAvatarNotFoundException if the avatar cannot be found in the repository.
   */
  @Override
  public void remove(long userId) {
    Avatar avatar =
        avatarRepository
            .findByUserId(userId)
            .orElseThrow(() -> new UserAvatarNotFoundException("The user doesn't have an avatar"));
    avatarRepository.delete(avatar);
  }

  /**
   * Generates a base path for the avatar file. Encodes the file in Base64 and prepends a data URI
   * scheme. If the file cannot be read, it throws an exception.
   *
   * @param avatarFile the avatar file to generate the base path for.
   * @return a String representing the base path for the avatar file.
   * @throws MissingFileDataException if the file cannot be read.
   */
  private String getBasePath(MultipartFile avatarFile) {
    String avatarBasePath = null;
    try {
      avatarBasePath = Base64.getEncoder().encodeToString(avatarFile.getBytes());
      return "data:" + avatarFile.getContentType() + ";base64," + avatarBasePath;
    } catch (IOException e) {
      throw new MissingFileDataException("Cannot read the file");
    }
  }

  private String getBaseAvatar() {
    try (InputStream in = getClass().getResourceAsStream("/img/default.jpg")) {
      if (in == null) {
        throw new MissingFileDataException(
            "Cannot read a file: default.jpg not found in classpath");
      }
      byte[] fileContent = IOUtils.toByteArray(in);
      String encodedString = Base64.getEncoder().encodeToString(fileContent);
      return "data:image/jpeg;base64," + encodedString;
    } catch (IOException e) {
      throw new MissingFileDataException("Cannot read a file");
    }
  }
}
