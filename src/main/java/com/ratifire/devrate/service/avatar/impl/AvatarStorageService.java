package com.ratifire.devrate.service.avatar.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.ratifire.devrate.dto.AvatarDto;
import com.ratifire.devrate.entity.Avatar;
import com.ratifire.devrate.exception.MissingFileDataException;
import com.ratifire.devrate.exception.UserAvatarAlreadyExistException;
import com.ratifire.devrate.exception.UserAvatarNotFoundException;
import com.ratifire.devrate.repository.AvatarRepository;
import com.ratifire.devrate.service.avatar.AvatarService;
import com.ratifire.devrate.util.avatar.AvatarUtils;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for managing user avatar storage in a cloud storage solution. Provides functionality to
 * add, update, and remove user avatars, as well as to retrieve the path to a user's avatar. It
 * integrates with an external cloud storage service (e.g., Google Cloud Storage) for the physical
 * storage of avatar files.
 */
@Service
@Profile("default")
public class AvatarStorageService implements AvatarService {

  private final Storage storage;
  private final AvatarUtils avatarUtils;
  private final AvatarRepository avatarRepository;

  @Value("${storage.domain}")
  private String domain;

  @Value("${storage.bucket.name}")
  private String bucketName;

  @Value("${storage.default.avatar.path}")
  private String defaultAvatar;

  /**
   * Constructs a new AvatarStorageService with the necessary dependencies.
   *
   * @param storage the cloud storage service interface
   * @param avatarUtils utilities for avatar file handling and validation
   * @param avatarRepository repository for accessing and persisting avatar data
   */
  public AvatarStorageService(
      Storage storage, AvatarUtils avatarUtils, AvatarRepository avatarRepository) {
    this.storage = storage;
    this.avatarUtils = avatarUtils;
    this.avatarRepository = avatarRepository;
  }

  /**
   * Retrieves the path to a user's avatar. If the user does not have an avatar set, a default
   * avatar path is returned.
   *
   * @param userId the ID of the user whose avatar path is to be retrieved
   * @return AvatarDto containing the path to the newly stored avatar
   */
  @Override
  public AvatarDto get(long userId) {
    Optional<Avatar> avatar = avatarRepository.findByUserId(userId);
    return avatar
        .map(value -> new AvatarDto(value.getPath()))
        .orElseGet(() -> new AvatarDto(defaultAvatar));
  }

  /**
   * Adds a new avatar for a user. Validates the avatar file and stores it in cloud storage. If the
   * user already has an avatar, a {@link UserAvatarAlreadyExistException} is thrown.
   *
   * @param userId the ID of the user for whom the avatar is to be added
   * @param avatarFile the avatar file to be stored
   * @return AvatarDto containing the path to the newly stored avatar
   * @throws UserAvatarAlreadyExistException if the user already has an avatar
   */
  @Override
  public AvatarDto add(long userId, MultipartFile avatarFile) {
    if (avatarRepository.existsByUserId(userId)) {
      throw new UserAvatarAlreadyExistException("The user already has an avatar");
    }
    avatarUtils.fileValidation(avatarFile);

    String avatarPath = saveToStorage(userId, avatarFile);

    Avatar avatar =
        Avatar.builder()
            .path(avatarPath)
            .fileName(avatarPath.substring(avatarPath.lastIndexOf('/') + 1))
            .userId(userId)
            .build();

    avatarRepository.save(avatar);

    return new AvatarDto(avatarPath);
  }

  /**
   * Updates the avatar for a user. Validates the new avatar file, replaces the existing avatar in
   * cloud storage, and updates the user's avatar information in the database.
   *
   * @param userId the ID of the user whose avatar is to be updated
   * @param avatarFile the new avatar file to be stored
   * @return AvatarDto containing the path to the updated avatar
   * @throws UserAvatarNotFoundException if the user does not have an avatar to update
   */
  @Override
  public AvatarDto update(long userId, MultipartFile avatarFile) {
    Avatar avatar =
        avatarRepository
            .findByUserId(userId)
            .orElseThrow(() -> new UserAvatarNotFoundException("Avatar to update was not found"));
    avatarUtils.fileValidation(avatarFile);
    removeFromStorage(userId + "/" + avatar.getFileName());
    String avatarPath = saveToStorage(userId, avatarFile);

    avatar.setPath(avatarPath);
    avatar.setFileName(avatarPath.substring(avatarPath.lastIndexOf('/') + 1));

    avatarRepository.save(avatar);

    return new AvatarDto(avatarPath);
  }

  /**
   * Removes the avatar for a user. Deletes the avatar from cloud storage and the user's avatar
   * information from the database.
   *
   * @param userId the ID of the user whose avatar is to be removed
   * @throws UserAvatarNotFoundException if the user does not have an avatar to remove
   */
  @Override
  public void remove(long userId) {
    Avatar avatar =
        avatarRepository
            .findByUserId(userId)
            .orElseThrow(() -> new UserAvatarNotFoundException("The user doesn't have an avatar"));
    removeFromStorage(userId + "/" + avatar.getFileName());
    avatarRepository.delete(avatar);
  }

  /**
   * Removes an avatar file from cloud storage. This method is called when a user's avatar is being
   * deleted or replaced. It ensures that the avatar file is permanently removed from the cloud
   * storage bucket to free up space and maintain consistency.
   *
   * @param filePath the path of the file to remove from cloud storage. This is typically a path
   *     that includes the user's ID and the original file name to ensure uniqueness.
   */
  private void removeFromStorage(String filePath) {
    BlobId blobId = BlobId.of(bucketName, filePath);
    storage.delete(blobId);
  }

  /**
   * Saves an avatar file to cloud storage and returns the path where the file is stored. This
   * method is responsible for generating a unique file name, creating a blob in the cloud storage
   * bucket, and uploading the file's bytes.
   *
   * @param userId the ID of the user to whom the avatar belongs. This is used to generate the path
   *     where the avatar will be stored.
   * @param file the MultipartFile object containing the avatar to be saved. The file's bytes are
   *     read and uploaded to cloud storage.
   * @return the full path to the stored avatar file, including the domain and the unique file name.
   */
  @SuppressWarnings("checkstyle:LineLength")
  private String saveToStorage(long userId, MultipartFile file) {
    String objectName =
        userId + "/" + avatarUtils.generateUniqueFileName(file.getOriginalFilename());

    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    try {
      storage.create(blobInfo, file.getBytes());
    } catch (IOException e) {
      throw new MissingFileDataException("Cannot read the file");
    }
    return domain + bucketName + "/" + objectName;
  }
}
