package com.ratifire.devrate.service.avatar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.AvatarDto;
import com.ratifire.devrate.entity.Avatar;
import com.ratifire.devrate.exception.UserAvatarAlreadyExistException;
import com.ratifire.devrate.exception.UserAvatarNotFoundException;
import com.ratifire.devrate.repository.AvatarRepository;
import com.ratifire.devrate.service.avatar.impl.AvatarLocalService;
import com.ratifire.devrate.util.avatar.AvatarUtils;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/** Unit tests for the AvatarLocalService class. */
@ExtendWith(MockitoExtension.class)
public class AvatarLocalServiceTest {

  @Mock private AvatarRepository mockAvatarRepository;

  @Mock private AvatarUtils mockAvatarUtils;

  @InjectMocks private AvatarLocalService avatarLocalService;

  @Test
  public void testGetWithExistingAvatar() {

    Avatar existingAvatar = new Avatar();
    existingAvatar.setPath("path/to/avatar");
    when(mockAvatarRepository.findByUserId(anyLong())).thenReturn(Optional.of(existingAvatar));

    AvatarDto result = avatarLocalService.get(1L);

    assertEquals("path/to/avatar", result.getAvatarUrl());
  }

  @Test
  public void testGetWithDefaultAvatar() {

    when(mockAvatarRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

    AvatarDto result = avatarLocalService.get(1L);

    assertTrue(result.getAvatarUrl().startsWith("data:image/jpeg;base64,"));
  }

  @Test
  public void testAddAvatarSuccessfully() {

    when(mockAvatarRepository.existsByUserId(anyLong())).thenReturn(false);
    when(mockAvatarUtils.generateUniqueFileName(anyString())).thenReturn("uniqueFileName");
    MultipartFile mockFile =
        new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[10]);

    AvatarDto result = avatarLocalService.add(1L, mockFile);

    assertNotNull(result);
    verify(mockAvatarRepository, times(1)).save(any(Avatar.class));
  }

  @Test
  public void testAddAvatarFailsWhenAvatarExists() {

    when(mockAvatarRepository.existsByUserId(anyLong())).thenReturn(true);

    MultipartFile mockFile =
        new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[10]);
    assertThrows(UserAvatarAlreadyExistException.class, () -> avatarLocalService.add(1L, mockFile));
  }

  @Test
  public void testUpdateAvatarSuccessfully() {

    Avatar existingAvatar = new Avatar();
    existingAvatar.setUserId(1L);
    when(mockAvatarRepository.findByUserId(anyLong())).thenReturn(Optional.of(existingAvatar));
    when(mockAvatarUtils.generateUniqueFileName(anyString())).thenReturn("uniqueFileName");
    MultipartFile mockFile =
        new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[10]);

    AvatarDto result = avatarLocalService.update(1L, mockFile);

    assertNotNull(result);
    verify(mockAvatarRepository, times(1)).save(any(Avatar.class));
  }

  @Test
  public void testUpdateAvatarFailsWhenNoAvatarFound() {

    when(mockAvatarRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

    MultipartFile mockFile =
        new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[10]);
    assertThrows(UserAvatarNotFoundException.class, () -> avatarLocalService.update(1L, mockFile));
  }

  @Test
  public void testRemoveAvatarSuccessfully() {

    Avatar existingAvatar = new Avatar();
    existingAvatar.setUserId(1L);
    when(mockAvatarRepository.findByUserId(anyLong())).thenReturn(Optional.of(existingAvatar));

    avatarLocalService.remove(1L);

    verify(mockAvatarRepository, times(1)).delete(any(Avatar.class));
  }

  @Test
  public void testRemoveAvatarFailsWhenNoAvatarFound() {

    when(mockAvatarRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

    assertThrows(UserAvatarNotFoundException.class, () -> avatarLocalService.remove(1L));
  }
}
