package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.ChatMessageDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.exception.EmploymentRecordNotFoundException;
import com.ratifire.devrate.mapper.impl.chat.MessageMapper;
import com.ratifire.devrate.repository.chat.MessageRepository;
import com.ratifire.devrate.repository.chat.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final TopicRepository topicRepository;
  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;

  public List<ChatMessageDto> findAllMessagesByTopicName(String topicName) {
    return messageMapper.toDto(messageRepository.findAllMessagesByTopicName(topicName));
  }
}
