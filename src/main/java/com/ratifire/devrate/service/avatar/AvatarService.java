package com.ratifire.devrate.service.avatar;

import com.ratifire.devrate.dto.AvatarDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * Defines the contract for services managing user avatars. Implementations of this interface
 * are responsible for providing mechanisms to add, update, retrieve, and delete avatars associated
 * with user accounts. The actual storage and retrieval mechanisms (e.g., cloud storage, local file
 * system, database) are abstracted away by the implementations.
 */
public interface AvatarService {

  AvatarDto get(long userId);

  AvatarDto add(long userId, MultipartFile avatarFile);

  AvatarDto update(long userId, MultipartFile avatarFile);

  void remove(long userId);
}
