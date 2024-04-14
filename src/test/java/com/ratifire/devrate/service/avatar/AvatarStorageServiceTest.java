package com.ratifire.devrate.service.avatar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.ratifire.devrate.dto.AvatarDto;
import com.ratifire.devrate.entity.Avatar;
import com.ratifire.devrate.exception.UserAvatarAlreadyExistException;
import com.ratifire.devrate.exception.UserAvatarNotFoundException;
import com.ratifire.devrate.repository.AvatarRepository;
import com.ratifire.devrate.service.avatar.impl.AvatarStorageService;
import com.ratifire.devrate.util.avatar.AvatarUtils;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

/** Unit tests for the AvatarStorageService class. */
@ExtendWith(MockitoExtension.class)
public class AvatarStorageServiceTest {

  @Mock private Storage mockStorage;
  @Mock private AvatarUtils mockAvatarUtils;
  @Mock private AvatarRepository mockAvatarRepository;
  @InjectMocks private AvatarStorageService service;

  /**
   * Sets up the test environment before each test. Initializes the AvatarStorageService with mocked
   * dependencies and configures necessary properties. This setup includes defining the bucket name,
   * domain, and default avatar path for the service.
   */
  @BeforeEach
  public void setUp() {
    service = new AvatarStorageService(mockStorage, mockAvatarUtils, mockAvatarRepository);
    ReflectionTestUtils.setField(service, "bucketName", "devrate-avatars");
    ReflectionTestUtils.setField(service, "domain", "https://storage.googleapis.com/");
    ReflectionTestUtils.setField(
        service,
        "defaultAvatar",
        "https://storage.googleapis.com/devrate-avatars/default/defaultAvatar.png");
  }

  @Test
  public void testGetExistingAvatar() {

    Avatar existingAvatar = new Avatar();
    existingAvatar.setPath("path/to/avatar.jpg");
    when(mockAvatarRepository.findByUserId(1L)).thenReturn(Optional.of(existingAvatar));

    AvatarDto result = service.get(1L);

    assertEquals("path/to/avatar.jpg", result.getAvatarUrl());
  }

  @Test
  public void testGetDefaultAvatar() {

    when(mockAvatarRepository.findByUserId(1L)).thenReturn(Optional.empty());

    AvatarDto result = service.get(1L);

    assertEquals(
        "https://storage.googleapis.com/devrate-avatars/default/defaultAvatar.png",
        result.getAvatarUrl());
  }

  @Test
  public void testAddAvatarWhenAlreadyExists() {

    when(mockAvatarRepository.existsByUserId(1L)).thenReturn(true);

    assertThrows(
        UserAvatarAlreadyExistException.class,
        () ->
            service.add(1L, new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[10])));
  }

  @Test
  public void testAddAvatarSuccessfully() {

    when(mockAvatarRepository.existsByUserId(1L)).thenReturn(false);
    when(mockAvatarUtils.generateUniqueFileName(anyString())).thenReturn("uniqueFileName.jpg");
    when(mockStorage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(null);

    MockMultipartFile file =
        new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());

    AvatarDto result = service.add(1L, file);

    assertNotNull(result);
    assertTrue(result.getAvatarUrl().contains("uniqueFileName.jpg"));
    verify(mockStorage, times(1)).create(any(BlobInfo.class), any(byte[].class));
    verify(mockAvatarRepository, times(1)).save(any(Avatar.class));
  }

  @Test
  public void testUpdateNonExistingAvatar() {

    when(mockAvatarRepository.findByUserId(1L)).thenReturn(Optional.empty());

    assertThrows(
        UserAvatarNotFoundException.class,
        () ->
            service.update(
                1L, new MockMultipartFile("file", "update.jpg", "image/jpeg", new byte[10])));
  }

  @Test
  public void testUpdateExistingAvatar() {

    Avatar existingAvatar = new Avatar();
    existingAvatar.setUserId(1L);
    existingAvatar.setFileName("old.jpg");
    existingAvatar.setPath("path/to/old.jpg");
    when(mockAvatarRepository.findByUserId(1L)).thenReturn(Optional.of(existingAvatar));
    when(mockAvatarUtils.generateUniqueFileName(anyString())).thenReturn("updatedFileName.jpg");
    when(mockStorage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(null);

    MockMultipartFile file =
        new MockMultipartFile("file", "update.jpg", "image/jpeg", "content".getBytes());

    AvatarDto result = service.update(1L, file);

    assertNotNull(result);
    assertTrue(result.getAvatarUrl().contains("updatedFileName.jpg"));
    verify(mockStorage, times(1)).delete(any(BlobId.class));
    verify(mockStorage, times(1)).create(any(BlobInfo.class), any(byte[].class));
    verify(mockAvatarRepository, times(1)).save(any(Avatar.class));
  }

  @Test
  public void testRemoveNonExistingAvatar() {

    when(mockAvatarRepository.findByUserId(1L)).thenReturn(Optional.empty());

    assertThrows(UserAvatarNotFoundException.class, () -> service.remove(1L));
  }

  @Test
  public void testRemoveExistingAvatar() {

    Avatar existingAvatar = new Avatar();
    existingAvatar.setUserId(1L);
    existingAvatar.setFileName("old.jpg");
    existingAvatar.setPath("path/to/old.jpg");
    when(mockAvatarRepository.findByUserId(1L)).thenReturn(Optional.of(existingAvatar));

    service.remove(1L);

    verify(mockStorage, times(1)).delete(any(BlobId.class));
    verify(mockAvatarRepository, times(1)).delete(any(Avatar.class));
  }
}
