package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between Notification entities and DTOs.
 */
@Mapper(componentModel = "spring")
public abstract class NotificationMapper implements DataMapper<NotificationDto, Notification> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  public abstract Notification toEntity(NotificationDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  public abstract Notification updateEntity(NotificationDto dto,
      @MappingTarget Notification entity);
}
