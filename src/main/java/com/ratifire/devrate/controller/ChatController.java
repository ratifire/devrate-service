package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.ChatMessageDto;
import com.ratifire.devrate.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

  private final ChatService chatService;

  @GetMapping("/{topicName}")
  public List<ChatMessageDto> getById(@PathVariable String topicName) {
    return chatService.findAllMessagesByTopicName(topicName);
  }
}
