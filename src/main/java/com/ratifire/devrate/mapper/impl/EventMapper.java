package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.EventDto;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;

/**
 * Interface for mapping Event entities to Event Dto.
 */
@Mapper(componentModel = "spring")
public abstract class EventMapper implements DataMapper<EventDto, Event> {

}
