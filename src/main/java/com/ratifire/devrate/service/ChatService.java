package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.ChatMessageDto;
import com.ratifire.devrate.dto.TopicDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.chat.Message;
import com.ratifire.devrate.entity.chat.Topic;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.repository.chat.MessageRepository;
import com.ratifire.devrate.repository.chat.TopicRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResourceAccessException;

/**
 * Service class for mapping chat topics and messages.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

  private final TopicRepository topicRepository;
  private final UserContextProvider userContextProvider;
  private final UserService userService;
  private final MessageRepository messageRepository;

  /**
   * Retrieves all topics for the authenticated user.
   *
   * @return a list of TopicDto objects representing the topics.
   */
  public List<TopicDto> getAllTopics() {
    long userId = userContextProvider.getAuthenticatedUserId();
    List<Topic> topics = topicRepository.findTopicsByUserId(userId);

    // remove the auth user topic
    topics.removeIf(
        topic -> topic.getUsers().size() == 1 && topic.getUsers().getFirst().getId() == userId);
    // remove the auth user id from each topic and leave only opponent user id
    topics.forEach(topic -> topic.getUsers().removeIf(user -> user.getId() == userId));

    return topics.stream()
        .map(this::convertToDto)
        .toList();
  }

  /**
   * Creates a new combined topic for the authenticated users if it does not already exist.
   *
   * @param topicName the name of the topic to create.
   * @param opponentUserId (optional) the ID of the opponent user to associate with the topic.
   */
  public void createTopic(long topicName, Long opponentUserId) {
    long userId = userContextProvider.getAuthenticatedUserId();
    if (topicRepository.existsByTopicNameAndUserId(topicName, userId)) {
      log.info("Topic name: {} already exists for the auth user id {}", topicName, userId);
      return;
    }

    List<User> users = new ArrayList<>();
    users.add(userService.findById(userId));
    if (opponentUserId != null) {
      users.add(userService.findById(opponentUserId));
    }

    topicRepository.save(Topic.builder()
        .topicName(topicName)
        .messages(new ArrayList<>())
        .users(users)
        .build());
  }

  /**
   * Creates a new topic for the authenticated user if it does not already exist.
   *
   * @param topicName the name of the topic to create.
   * @param user the auth user to associate with the topic.
   */
  public void createAuthTopic(long topicName, User user) {
    if (topicRepository.existsTopicByTopicName(topicName)) {
      log.info("Topic name: {} already exists", topicName);
      return;
    }

    List<User> users = new ArrayList<>();
    users.add(user);
    topicRepository.save(Topic.builder()
        .topicName(topicName)
        .messages(new ArrayList<>())
        .users(users)
        .build());
  }

  /**
   * Retrieves all messages for a specific topic and authenticated user.
   *
   * @param topicName the name of the topic to retrieve messages from.
   * @return a list of ChatMessageDto objects representing the messages.
   */
  public List<ChatMessageDto> findAllMessagesByTopicId(long topicName) {
    Topic combinedTopic = topicRepository.findTopicByTopicName(topicName)
        .orElseThrow(() -> new ResourceNotFoundException("Topic name " + topicName + " not found"));

    validateUserAccess(combinedTopic);

    return combinedTopic.getMessages() == null
        ? Collections.emptyList()
        : combinedTopic.getMessages().stream().map(this::convertToDto).toList();
  }

  /**
   * Creates a new message and associates it with a topic.
   *
   * @param chatMessageDto the ChatMessageDto object containing the message details.
   */
  public void createMessage(ChatMessageDto chatMessageDto) {
    Topic topic = topicRepository.findTopicByTopicName(chatMessageDto.getTopicName())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Topic name: " + chatMessageDto.getTopicName() + " not found"));

    validateUserAccess(topic);

    User sender = userService.findById(chatMessageDto.getSenderId());
    User receiver = userService.findById(chatMessageDto.getReceiverId());

    messageRepository.save(Message.builder()
        .payload(chatMessageDto.getPayload())
        .sentAt(chatMessageDto.getDateTime())
        .sender(sender)
        .receiver(receiver)
        .topic(topic)
        .status(chatMessageDto.getStatus())
        .build());
  }


  private TopicDto convertToDto(Topic topic) {
    // check that topic has users and remove authentication user from it
    long opponentUserId = topic.getUsers().stream()
        .mapToLong(User::getId)
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("No other user found in the topic"));

    Message lastMessage =
        CollectionUtils.isEmpty(topic.getMessages()) ? null : topic.getMessages().getLast();

    return TopicDto.builder()
        .topicName(topic.getTopicName())
        .userId(opponentUserId)
        .lastMessage(lastMessage != null ? lastMessage.getPayload() : null)
        .lastMessageDate(lastMessage != null ? lastMessage.getSentAt() : null)
        .build();
  }

  private ChatMessageDto convertToDto(Message message) {
    return ChatMessageDto.builder()
        .payload(message.getPayload())
        .dateTime(message.getSentAt())
        .senderId(message.getSender().getId())
        .receiverId(message.getReceiver().getId())
        .topicName(message.getTopic().getTopicName())
        .status(message.getStatus())
        .build();
  }

  private void validateUserAccess(Topic topic) {
    long userId = userContextProvider.getAuthenticatedUserId();
    if (topic.getUsers() == null
        || topic.getUsers().stream().noneMatch(user -> user.getId() == userId)) {
      throw new ResourceAccessException(
          String.format("User with ID %d does not have access to the topic.", userId));
    }
  }
}
