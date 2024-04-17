package com.ratifire.devrate.service.employmentrecord;

import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.exception.EducationNotFoundException;
import com.ratifire.devrate.exception.EmploymentRecordNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.EmploymentRecordRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user EmploymentRecord inform. Provides functionality for
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
   * @param id the ID of the user
   * @return the user's EmploymentRecord as a DTO
   * @throws EmploymentRecordNotFoundException if work experience information is not found
   */
  public EmploymentRecordDto findById(long id) {
    return employmentRecordRepository.findById(id).map(employmentRecordDataMapper::toDto)
        .orElseThrow(() -> new EducationNotFoundException("Education not found with id: " + id));
  }

  /**
   * Updates user EmploymentRecord information.
   *
   * @param employmentRecordDto the updated user's EmploymentRecord information as a DTO
   * @return the updated user work experience information as a DTO
   * @throws EmploymentRecordNotFoundException if the user work experience info does not exist by
   *                                             user id
   */
  public EmploymentRecordDto update(EmploymentRecordDto employmentRecordDto) {
    long id = employmentRecordDto.getId();
    Optional<EmploymentRecord> optionalEmploymentRecord = employmentRecordRepository.findById(id);

    return optionalEmploymentRecord.map(employmentRecord -> {
      employmentRecordDataMapper.updateEntity(employmentRecordDto, employmentRecord);
      EmploymentRecord updatedRecord = employmentRecordRepository.save(employmentRecord);
      return employmentRecordDataMapper.toDto(updatedRecord);
    }).orElseThrow(() -> new EmploymentRecordNotFoundException("User Id " + id));
  }

  /**
   * Deletes user EmploymentRecord information by user ID.
   *
   * @param id the ID of the user whose EmploymentRecord information is to be deleted
   * @throws EmploymentRecordNotFoundException if user EmploymentRecord information is not found
   */
  public void deleteById(long id) {
    employmentRecordRepository.deleteById(id);
  }


  /**
   * Retrieves EmploymentRecord (work experience) information by user ID.
   *
   * @param userId the ID of the user
   * @return the user's work experience as a DTO
   * @throws EmploymentRecordNotFoundException if work experience information is not found
   */
  public List<EmploymentRecordDto> getEmploymentRecordsByUserId(long userId) {
    List<EmploymentRecord> employmentRecords = employmentRecordRepository.findByUserId(userId);

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
  public EmploymentRecordDto createEmploymentRecord(EmploymentRecordDto employmentRecordDto,
      long userId) {
    EmploymentRecord employmentRecord = employmentRecordDataMapper.toEntity(employmentRecordDto);
    employmentRecord.setUserId(userId);
    return employmentRecordDataMapper.toDto(employmentRecordRepository.save(employmentRecord));
  }
}
