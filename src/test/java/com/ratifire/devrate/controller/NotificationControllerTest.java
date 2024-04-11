package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ratifire.devrate.configuration.SecurityConfiguration;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.util.websocket.WebSocketSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests for the {@link NotificationController} class.
 */
@WebMvcTest(NotificationController.class)
@Import(SecurityConfiguration.class)
public class NotificationControllerTest {

  @MockBean
  private NotificationService notificationService;
  @MockBean
  private UserSecurityService userSecurityService;
  @MockBean
  private WebSocketSender webSocketSender;
  @MockBean
  private UserDetailsService userDetailsService;

  @Autowired
  private MockMvc mockMvc;

  private int testUserId;
  private int testNotificationId;
  private String testEmail;
  private UserSecurity testUserSecurity;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void init() {
    testUserId = 123;
    testNotificationId = 456;
    testEmail = "test@example.com";

    testUserSecurity = UserSecurity.builder()
        .email(testEmail)
        .build();
  }

  @Test
  @WithMockUser
  public void testRead() throws Exception {
    doNothing().when(notificationService).markAsReadById(anyLong());
    when(userSecurityService.getByUserId(anyLong())).thenReturn(testUserSecurity);
    doNothing().when(webSocketSender).sendNotificationsByUserEmail(any());

    mockMvc.perform(patch("/notifications")
            .param("userId", String.valueOf(testUserId))
            .param("notificationId", String.valueOf(testNotificationId)))
        .andExpect(status().isOk());

    verify(notificationService, times(1)).markAsReadById(testNotificationId);
    verify(userSecurityService, times(1)).getByUserId(testUserId);
    verify(webSocketSender, times(1)).sendNotificationsByUserEmail(testEmail);
  }

  @Test
  @WithMockUser
  public void testDelete() throws Exception {
    doNothing().when(notificationService).deleteById(anyLong());
    when(userSecurityService.getByUserId(anyLong())).thenReturn(testUserSecurity);
    doNothing().when(webSocketSender).sendNotificationsByUserEmail(any());

    mockMvc.perform(delete("/notifications")
            .param("userId", String.valueOf(testUserId))
            .param("notificationId", String.valueOf(testNotificationId)))
        .andExpect(status().isOk());

    verify(notificationService, times(1)).deleteById(testNotificationId);
    verify(userSecurityService, times(1)).getByUserId(testUserId);
    verify(webSocketSender, times(1)).sendNotificationsByUserEmail(testEmail);
  }
}
