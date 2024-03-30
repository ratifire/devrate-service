package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;

/**
 * Mapper interface for mapping between Contact and ContactDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class ContactMapper implements DataMapper<ContactDto, Contact> {

}
