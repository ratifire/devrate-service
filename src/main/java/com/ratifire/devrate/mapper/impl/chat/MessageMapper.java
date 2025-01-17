package com.ratifire.devrate.mapper.impl.chat;

import com.ratifire.devrate.dto.ChatMessageDto;
import com.ratifire.devrate.entity.chat.Message;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.service.ChatService;
import com.ratifire.devrate.service.MasteryService;
import com.ratifire.devrate.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between Bookmark and BookmarkDto objects.
 */
@Mapper(componentModel = "spring", uses = {UserService.class})
public abstract class MessageMapper implements DataMapper<ChatMessageDto, Message> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sentAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "sender", source = "chatMessageDto.sender",
          qualifiedByName = {"UserService", "findById"})
  @Mapping(target = "chatMessageDto.receiver", source = "receiver.id")
  public abstract Message toEntity(ChatMessageDto chatMessageDto);
}
