package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ratifire.devrate.dto.AvatarDto;
import com.ratifire.devrate.service.avatar.AvatarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

/** Unit tests for the AvatarController class. */
@WebMvcTest(AvatarController.class)
class AvatarControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AvatarService avatarService;

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void getAvatarPath_ShouldReturnAvatarPath() throws Exception {
    long userId = 1L;
    String expectedPath = "http://example.com/avatar.jpg";
    when(avatarService.get(1L)).thenReturn(new AvatarDto(expectedPath));

    mockMvc
        .perform(get("/avatar/{userId}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.avatarUrl").value(expectedPath));

    verify(avatarService).get(userId);
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void addAvatar_ShouldAddAvatarAndReturnUrl() throws Exception {
    long userId = 1L;
    MockMultipartFile avatarFile =
        new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "avatar content".getBytes());
    String expectedUrl = "http://example.com/avatar.jpg";
    when(avatarService.add(eq(userId), any(MultipartFile.class)))
        .thenReturn(new AvatarDto(expectedUrl));

    mockMvc
        .perform(
            multipart("/avatar/{userId}", userId)
                .file(avatarFile)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.avatarUrl").value(expectedUrl));

    verify(avatarService).add(eq(userId), any(MultipartFile.class));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void updateAvatar_ShouldUpdateAvatarAndReturnUrl() throws Exception {
    long userId = 1L;
    MockMultipartFile avatarFile =
        new MockMultipartFile(
            "avatar", "new-avatar.jpg", "image/jpeg", "new avatar content".getBytes());
    String expectedUrl = "http://example.com/new-avatar.jpg";

    when(avatarService.update(eq(userId), any(MultipartFile.class)))
        .thenReturn(new AvatarDto(expectedUrl));

    mockMvc
        .perform(
            multipart("/avatar/{userId}", userId)
                .file(avatarFile)
                .with(csrf())
                .with(
                    request -> {
                      request.setMethod("PUT");
                      return request;
                    })
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.avatarUrl").value(expectedUrl));

    verify(avatarService).update(eq(userId), any(MultipartFile.class));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void removeAvatar_ShouldRemoveAvatar() throws Exception {
    long userId = 1L;
    doNothing().when(avatarService).remove(userId);

    mockMvc.perform(delete("/avatar/{userId}", userId).with(csrf())).andExpect(status().isOk());

    verify(avatarService).remove(userId);
  }
}
