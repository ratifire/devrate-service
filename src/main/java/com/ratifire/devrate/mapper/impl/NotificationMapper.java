package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between Notification entities and DTOs.
 */
@Mapper(componentModel = "spring")
public abstract class NotificationMapper implements DataMapper<NotificationDto, Notification> {

  @Mapping(target = "userId", source = "entity.user.id")
  public abstract NotificationDto toDto(Notification entity);
}
