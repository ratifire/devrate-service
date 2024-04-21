package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.mapper.DataMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Interface for mapping EmploymentRecord Dto to EmploymentRecord entities.
 */
@Mapper(componentModel = "spring")
public abstract class EmploymentRecordMapper implements
    DataMapper<EmploymentRecordDto, EmploymentRecord> {

  @Mapping(target = "id", ignore = true)
  public abstract EmploymentRecord toEntity(EmploymentRecordDto employmentRecordDto);

  @Mapping(target = "id", ignore = true)
  public abstract EmploymentRecord updateEntity(EmploymentRecordDto employmentRecordDto,
      @MappingTarget EmploymentRecord employmentRecord);
}
