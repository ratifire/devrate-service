package com.ratifire.devrate.mapper;

import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.entity.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for mapping between Contact and ContactDto objects.
 */
@Mapper(componentModel = "spring")
public interface ContactMapper {

  ContactDto toDto(Contact contact);

  Contact toEntity(ContactDto contactDto);

  void toUpdate(ContactDto contactDto, @MappingTarget Contact contact);

}
