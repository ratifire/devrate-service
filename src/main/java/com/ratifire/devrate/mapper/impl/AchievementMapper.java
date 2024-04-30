package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AchievementMapper implements DataMapper<AchievementDto, Achievement> {


}
