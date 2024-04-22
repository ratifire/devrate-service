package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ratifire.devrate.configuration.SecurityConfiguration;
import com.ratifire.devrate.service.NotificationService;
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
  private WebSocketSender webSocketSender;
  @MockBean
  private UserDetailsService userDetailsService;

  @Autowired
  private MockMvc mockMvc;

  private int testUserId;
  private int testNotificationId;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void init() {
    testUserId = 123;
    testNotificationId = 456;
  }

  @Test
  @WithMockUser
  public void testRead() throws Exception {
    doNothing().when(notificationService).markAsReadById(anyLong());
    doNothing().when(webSocketSender).sendNotificationsByUserId(anyLong());

    mockMvc.perform(patch("/notifications")
            .param("userId", String.valueOf(testUserId))
            .param("notificationId", String.valueOf(testNotificationId)))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  public void testDelete() throws Exception {
    doNothing().when(notificationService).deleteById(anyLong());
    doNothing().when(webSocketSender).sendNotificationsByUserId(anyLong());

    mockMvc.perform(delete("/notifications")
            .param("userId", String.valueOf(testUserId))
            .param("notificationId", String.valueOf(testNotificationId)))
        .andExpect(status().isOk());
  }
}
