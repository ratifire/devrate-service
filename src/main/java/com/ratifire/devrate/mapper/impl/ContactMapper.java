package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for mapping between Contact and ContactDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class ContactMapper implements DataMapper<ContactDto, Contact> {

  @Mapping(target = "id", ignore = true)
  public abstract Contact toEntity(ContactDto contactDto);

  @Mapping(target = "id", ignore = true)
  public abstract Contact updateEntity(ContactDto dto, @MappingTarget Contact entity);
}
