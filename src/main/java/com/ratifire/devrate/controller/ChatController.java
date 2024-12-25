package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller to communicate with websocket's topics.
 */
@Controller
@RequiredArgsConstructor
public class ChatController {

  private SimpMessagingTemplate simpMessagingTemplate;

  /**
   * Endpoint to communicate with websocket via messaging.
   *
   */
  @MessageMapping("/sengMessage")
  @SendTo("/topic/messages")
  public void sendMessage(@RequestBody ChatMessageDto chatMessageDto) {
    simpMessagingTemplate.convertAndSendToUser(chatMessageDto.getRecipient(),
            "queue/message", chatMessageDto);
  }

}
