package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.ChatMessageDto;
import com.ratifire.devrate.dto.TopicDto;
import com.ratifire.devrate.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing chat-related operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

  private final ChatService chatService;

  /**
   * Retrieves all topics for the authenticated user.
   *
   * @return a list of TopicDto objects representing the topics.
   */
  @GetMapping
  public List<TopicDto> getAllTopics() {
    return chatService.getAllTopics();
  }

  /**
   * Creates a new topic for the authenticated user.
   *
   * @param topicName the name of the topic to create.
   */
  @PostMapping("/{topicName}/{opponentUserId}")
  public void createTopic(@PathVariable long topicName, @PathVariable long opponentUserId) {
    chatService.createTopic(topicName, opponentUserId);
  }

  /**
   * Retrieves all messages for a specific topic.
   *
   * @param topicName the name of the topic to retrieve messages from.
   * @return a list of ChatMessageDto objects representing the messages.
   */
  @GetMapping("/{topicName}")
  public List<ChatMessageDto> getAllMessagesByTopicId(@PathVariable long topicName) {
    return chatService.findAllMessagesByTopicId(topicName);
  }
}
