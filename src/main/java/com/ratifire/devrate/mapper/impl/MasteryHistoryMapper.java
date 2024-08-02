package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.MasteryHistoryDto;
import com.ratifire.devrate.entity.MasteryHistory;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between MasteryHistory entities and MasteryHistoryDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class MasteryHistoryMapper implements
    DataMapper<MasteryHistoryDto, MasteryHistory> {

  @Mapping(target = "masteryId", source = "entity.mastery.id")
  public abstract MasteryHistoryDto toDto(MasteryHistory entity);
}