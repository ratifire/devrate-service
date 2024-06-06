package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for mapping between Skill and SkillDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class SkillMapper implements DataMapper<SkillDto, Skill> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "counter", ignore = true)
  @Mapping(target = "grows", ignore = true)
  public abstract Skill toEntity(SkillDto skillDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "counter", ignore = true)
  @Mapping(target = "grows", ignore = true)
  public abstract Skill updateEntity(SkillDto skillDto,
      @MappingTarget Skill skill);

  public abstract SkillDto toDto(Skill skill);
}