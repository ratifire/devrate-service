package com.ratifire.devrate.service.employmentrecord;

import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.exception.EmploymentRecordNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.EmploymentRecordRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user work experience information. Provides functionality for
 * searching, creating, updating, and deleting user work experience data in the database.
 */
@Service
@RequiredArgsConstructor
public class EmploymentRecordService {

  private final EmploymentRecordRepository employmentRecordRepository;
  private final DataMapper<EmploymentRecordDto, EmploymentRecord> employmentRecordDataMapper;

  /**
   * Retrieves EmploymentRecord (work experience) information by user ID.
   *
   * @param userId the ID of the user
   * @return the user's work experience as a DTO
   * @throws EmploymentRecordNotFoundException if work experience information is not found
   */
  public List<EmploymentRecordDto> findByUserId(long userId) {
    List<EmploymentRecord> employmentRecords = employmentRecordRepository.findByUserId(userId);
    if (employmentRecords.isEmpty()) {
      throw new EmploymentRecordNotFoundException("User Id" + userId);
    }

    return employmentRecords.stream()
        .map(employmentRecordDataMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Creates work experience information.
   *
   * @param employmentRecordDto the user's work experience information as a DTO
   * @return the created user work experience information as a DTO
   */
  public EmploymentRecordDto create(EmploymentRecordDto employmentRecordDto, long userId) {

    EmploymentRecord employmentRecord = employmentRecordDataMapper.toEntity(employmentRecordDto);
    employmentRecord.setUserId(userId);

    return employmentRecordDataMapper.toDto(employmentRecordRepository.save(employmentRecord));
  }

  /**
   * Updates user work experience information.
   *
   * @param employmentRecordDto the updated user's work experience information as a DTO
   * @return the updated user work experience information as a DTO
   * @throws EmploymentRecordNotFoundException if the user work experience info does not exist by
   *                                             user id
   */
  public EmploymentRecordDto update(EmploymentRecordDto employmentRecordDto) {
    long id = employmentRecordDto.getId();
    EmploymentRecord employmentRecord = employmentRecordRepository.findById(id)
        .orElseThrow(
            () -> new EmploymentRecordNotFoundException("User Id" + id));

    employmentRecordDataMapper.updateEntity(employmentRecordDto, employmentRecord);

    return employmentRecordDataMapper.toDto(employmentRecordRepository.save(employmentRecord));
  }

  /**
   * Deletes user work experience information by user ID.
   *
   * @param id the ID of the user whose work experience information is to be deleted
   * @throws EmploymentRecordNotFoundException if user work experience information is not found
   */
  public void delete(long id) {
    EmploymentRecord employmentRecord = employmentRecordRepository.findById(id)
        .orElseThrow(
            () -> new EmploymentRecordNotFoundException("id " + id));

    employmentRecordRepository.delete(employmentRecord);
  }

}
