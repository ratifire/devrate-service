package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.util.converter.JsonUtil;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * Mapper interface for mapping between Mastery and MasteryDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class MasteryMapper implements DataMapper<MasteryDto, Mastery> {

  private static final String DEFAULT_MASTERY_LEVELS_PATH =
      "/static/data/specialization/mastery-levels.json";

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "skills", ignore = true)
  @Mapping(target = "specialization", ignore = true)
  @Mapping(target = "level", source = "masteryDto.level", qualifiedByName = "getLevel")
  public abstract Mastery toEntity(MasteryDto masteryDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "skills", ignore = true)
  @Mapping(target = "specialization", ignore = true)
  @Mapping(target = "level", source = "masteryDto.level", qualifiedByName = "getLevel")
  public abstract Mastery updateEntity(MasteryDto masteryDto,
      @MappingTarget Mastery mastery);

  @Mapping(target = "level", source = "entity.level", qualifiedByName = "getMasteryLevel")
  public abstract MasteryDto toDto(Mastery entity);

  /**
   * Retrieves the numeric level associated with a given mastery level name.
   *
   * @param masteryLevel the name of the mastery level to search for
   * @return the numeric level corresponding to the given mastery level name, or {@code 0}
   */
  @Named("getLevel")
  public static int getLevel(String masteryLevel) {
    List<String> masteryLevels = JsonUtil.loadStringFromJson(DEFAULT_MASTERY_LEVELS_PATH);
    return IntStream.range(0, masteryLevels.size())
        .boxed()
        .collect(Collectors.toMap(i -> i + 1, masteryLevels::get))
        .entrySet().stream()
        .filter(entry -> entry.getValue().equals(masteryLevel))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(0);
  }

  /**
   * Retrieves the name of a mastery level associated with a given numeric level.
   *
   * @param level the numeric level to search for
   * @return the name of the mastery level corresponding to the given numeric level, or "Unknown"
   */
  @Named("getMasteryLevel")
  public static String getMasteryLevel(int level) {
    List<String> masteryLevels = JsonUtil.loadStringFromJson(DEFAULT_MASTERY_LEVELS_PATH);
    return IntStream.range(0, masteryLevels.size())
        .boxed()
        .collect(Collectors.toMap(i -> i + 1, masteryLevels::get))
        .getOrDefault(level, "Unknown");
  }
}
