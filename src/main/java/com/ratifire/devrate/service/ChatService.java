package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.ChatMessageDto;
import com.ratifire.devrate.dto.TopicDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.chat.Message;
import com.ratifire.devrate.repository.chat.MessageRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for handling chat operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

  private final UserContextProvider userContextProvider;
  private final UserService userService;
  private final MessageRepository messageRepository;

  /**
   * Retrieves the latest message from each conversation for the authenticated user.
   *
   * @return a list of {@link TopicDto} representing the chat topics.
   */
  public List<TopicDto> getUserChatTopics() {
    long authUserId = userContextProvider.getAuthenticatedUserId();
    return messageRepository.findLastMessagesByUserId(authUserId)
        .stream()
        .map(message -> {
          User opponent = message.getSender().getId() == authUserId
              ? message.getReceiver()
              : message.getSender();
          return toTopicDto(message, opponent);
        })
        .toList();
  }

  /**
   * Retrieves paginated conversation messages between the authenticated user and the opponent.
   *
   * @param opponentUserId the ID of the opponent user.
   * @param pageable pagination information.
   * @return a page of {@link ChatMessageDto} objects representing the conversation.
   */
  public Page<ChatMessageDto> getConversationMessages(long opponentUserId, Pageable pageable) {
    long authUserId = userContextProvider.getAuthenticatedUserId();

    Page<Message> messagesPage = messageRepository
        .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySentAtDesc(
            authUserId, opponentUserId,
            opponentUserId, authUserId,
            pageable);
    return messagesPage.map(this::toChatMessageDto);
  }

  /**
   * Creates and save a new message.
   *
   * @param chatMessageDto the ChatMessageDto object containing the message details.
   */
  public void createMessage(ChatMessageDto chatMessageDto) {
    User sender = userService.findById(chatMessageDto.getSenderId());
    User receiver = userService.findById(chatMessageDto.getReceiverId());

    messageRepository.save(Message.builder()
        .payload(chatMessageDto.getPayload())
        .sentAt(chatMessageDto.getDateTime())
        .sender(sender)
        .receiver(receiver)
        .status(chatMessageDto.getStatus())
        .build());
  }

  private ChatMessageDto toChatMessageDto(Message message) {
    return ChatMessageDto.builder()
        .senderId(message.getSender().getId())
        .receiverId(message.getReceiver().getId())
        .payload(message.getPayload())
        .status(message.getStatus())
        .dateTime(message.getSentAt())
        .build();
  }

  private TopicDto toTopicDto(Message message, User opponent) {
    return TopicDto.builder()
        .opponentUserId(opponent.getId())
        .opponentFirstName(opponent.getFirstName())
        .opponentLastName(opponent.getLastName())
        .opponentPicture(opponent.getPicture())
        .lastMessage(message.getPayload())
        .lastMessageDate(message.getSentAt())
        .build();
  }
}
