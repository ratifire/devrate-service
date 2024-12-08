package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.exception.EmploymentRecordNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.EmploymentRecordRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user EmploymentRecord inform. Provides functionality for
 * searching, creating, updating, and deleting user work experience data in the database.
 */
@Service
@RequiredArgsConstructor
public class EmploymentRecordService {

  private final EmploymentRecordRepository repository;
  private final DataMapper<EmploymentRecordDto, EmploymentRecord> mapper;

  /**
   * Retrieves EmploymentRecord (work experience) information by employment ID.
   *
   * @param id the ID of the employment
   * @return the user's EmploymentRecord as a DTO
   * @throws EmploymentRecordNotFoundException if work experience information is not found
   */
  public EmploymentRecordDto findById(long id) {
    return repository.findById(id).map(mapper::toDto)
        .orElseThrow(() -> new EmploymentRecordNotFoundException(id));
  }

  /**
   * Updates user EmploymentRecord information.
   *
   * @param id           The ID of the employment record entity to be updated.
   * @param employmentRecordDto the updated user's EmploymentRecord information as a DTO
   * @return the updated user EmploymentRecord information as a DTO
   * @throws EmploymentRecordNotFoundException if the user EmploymentRecord info does not exist by
   *                                             Employment record`s id
   */
  public EmploymentRecordDto update(long id, EmploymentRecordDto employmentRecordDto) {
    Optional<EmploymentRecord> optionalEmploymentRecord = repository.findById(id);

    return optionalEmploymentRecord.map(employmentRecord -> {
      mapper.updateEntity(employmentRecordDto, employmentRecord);
      EmploymentRecord updatedRecord = repository.save(employmentRecord);
      return mapper.toDto(updatedRecord);
    }).orElseThrow(() -> new EmploymentRecordNotFoundException(id));
  }

  /**
   * Deletes EmploymentRecord information by employment ID.
   *
   * @param id the ID of the employment whose EmploymentRecord information is to be deleted
   */
  public void delete(long id) {
    repository.deleteById(id);
  }
}
