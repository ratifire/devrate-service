package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.enums.MasteryLevel;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * Mapper interface for mapping between Mastery and MasteryDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class MasteryMapper implements DataMapper<MasteryDto, Mastery> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "skills", ignore = true)
  @Mapping(target = "specialization", ignore = true)
  @Mapping(target = "level", source = "masteryDto.level", qualifiedByName = "resolveLevelByName")
  public abstract Mastery toEntity(MasteryDto masteryDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "skills", ignore = true)
  @Mapping(target = "specialization", ignore = true)
  @Mapping(target = "level", source = "masteryDto.level", qualifiedByName = "resolveLevelByName")
  public abstract Mastery updateEntity(MasteryDto masteryDto,
      @MappingTarget Mastery mastery);

  @Mapping(target = "level", source = "entity.level", qualifiedByName = "resolveNameByLevel")
  public abstract MasteryDto toDto(Mastery entity);

  /**
   * Retrieves the numeric level associated with a given mastery level name.
   *
   * @param mastery the name of the mastery level to search for
   * @return the numeric level corresponding to the given mastery level name, or {@code 0}
   */
  @Named("resolveLevelByName")
  public static int resolveLevelByName(String mastery) {
    return MasteryLevel.getLevelByName(mastery);
  }

  /**
   * Retrieves the name of a mastery level associated with a given numeric level.
   *
   * @param level the numeric level to search for
   * @return the name of the mastery level corresponding to the given numeric level, or "Unknown"
   */
  @Named("resolveNameByLevel")
  public static String resolveNameByLevel(int level) {
    return MasteryLevel.getNameByLevel(level);
  }
}
