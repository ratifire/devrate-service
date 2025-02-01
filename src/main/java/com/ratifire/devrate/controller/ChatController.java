package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.ChatMessageDto;
import com.ratifire.devrate.dto.TopicDto;
import com.ratifire.devrate.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
   * Retrieves the latest message from each conversation for the authenticated user.
   *
   * @return a list of {@link TopicDto} representing the chat topics.
   */
  @GetMapping
  public List<TopicDto> getUserChatTopics() {
    return chatService.getUserChatTopics();
  }

  /**
   * Retrieves paginated conversation messages between the authenticated user and the opponent.
   *
   * @param opponentUserId the ID of the opponent user.
   * @param page the page number (starting from 0)
   * @param size the number of records per page
   * @return a page of {@link ChatMessageDto} objects representing the conversation.
   */
  @GetMapping("/{opponentUserId}")
  public Page<ChatMessageDto> getConversationMessages(@PathVariable long opponentUserId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return chatService.getConversationMessages(opponentUserId, PageRequest.of(page, size));
  }
}
