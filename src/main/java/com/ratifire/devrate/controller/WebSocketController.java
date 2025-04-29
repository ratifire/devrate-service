package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.ChatMessageDto;
import com.ratifire.devrate.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller to communicate with websocket's topics.
 */
@Controller
@AllArgsConstructor
public class WebSocketController {

  private SimpMessagingTemplate simpMessagingTemplate;
  private ChatService chatService;

  /**
   * Endpoint to communicate with websocket via messaging.
   */
  @MessageMapping("/chat")
  public void sendMessage(@RequestBody ChatMessageDto chatMessageDto) {
    simpMessagingTemplate.convertAndSendToUser(String.valueOf(chatMessageDto.getSenderId()),
            "/queue/messages", chatMessageDto);
    simpMessagingTemplate.convertAndSendToUser(String.valueOf(chatMessageDto.getReceiverId()),
            "/queue/messages", chatMessageDto);

    chatService.createMessage(chatMessageDto);
  }
}
