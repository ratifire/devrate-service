package com.ratifire.devrate.mapper;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.encoder.EncodedMapping;
import com.ratifire.devrate.mapper.encoder.PasswordEncoderMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface for mapping SignUpDto to User entities.
 */
@Mapper(componentModel = "spring", uses = PasswordEncoderMapper.class)
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "password", qualifiedBy = EncodedMapping.class)
  User toEntity(SignUpDto signUpDto, Role role);

  SignUpDto toDto(User user);
}
