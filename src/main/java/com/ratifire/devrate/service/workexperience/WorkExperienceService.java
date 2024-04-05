package com.ratifire.devrate.service.workexperience;

import com.ratifire.devrate.dto.WorkExperienceDto;
import com.ratifire.devrate.entity.WorkExperience;
import com.ratifire.devrate.exception.WorkExperienceAlreadyExistException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.WorkExperienceRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The service responsible for managing user work experience information. Provides functionality for
 * searching, creating, updating, and deleting user work experience data in the database.
 */
@Service
@RequiredArgsConstructor
public class WorkExperienceService {

  private final WorkExperienceRepository workExperienceRepository;
  private final DataMapper<WorkExperienceDto, WorkExperience> workExperienceDataMapper;

  /**
   * Retrieves work experience information by user ID.
   *
   * @param id the ID of the user
   * @return the user's work experience as a DTO
   * @throws WorkExperienceAlreadyExistException if work experience information is not found
   */
  public List<WorkExperienceDto> findByUserId(long id) {
    List<WorkExperience> workExperiences = workExperienceRepository.findByUserId(id);
    if (workExperiences.isEmpty()) {
      throw new WorkExperienceAlreadyExistException("The user's work experience information "
          + "could not be found with the user id \"" + id + "\"");
    }
    return workExperiences.stream()
        .map(workExperienceDataMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Creates work experience information.
   *
   * @param workExperienceDto the user's work experience information as a DTO
   * @return the created user work experience information as a DTO
   * @throws WorkExperienceAlreadyExistException if the user work experience info already exists
   */
  @Transactional
  public WorkExperienceDto create(WorkExperienceDto workExperienceDto) {
    long id = workExperienceDto.getUserId();

    WorkExperience workExperience = workExperienceDataMapper.toEntity(workExperienceDto);
    workExperience.setUserId(id);

    return workExperienceDataMapper.toDto(workExperienceRepository.save(workExperience));
  }

  /**
   * Updates user work experience information.
   *
   * @param workExperienceDto the updated user's work experience information as a DTO
   * @return the updated user work experience information as a DTO
   * @throws WorkExperienceAlreadyExistException if the user work experience info does not exist by
   *                                             user id
   */
  public WorkExperienceDto update(WorkExperienceDto workExperienceDto) {
    long id = workExperienceDto.getId();
    WorkExperience workExperience = workExperienceRepository.findById(id)
        .orElseThrow(
            () -> new WorkExperienceAlreadyExistException("The user's work experience information "
                + "could not be found with the user id \"" + id + "\""));

    workExperienceDataMapper.updateEntity(workExperienceDto, workExperience);

    return workExperienceDataMapper.toDto(workExperienceRepository.save(workExperience));
  }

  /**
   * Deletes user work experience information by user ID.
   *
   * @param id the ID of the user whose work experience information is to be deleted
   * @throws WorkExperienceAlreadyExistException if user work experience information is not found
   */
  public void delete(long id) {
    WorkExperience workExperience = workExperienceRepository.findById(id)
        .orElseThrow(
            () -> new WorkExperienceAlreadyExistException("The user's work experience information "
                + "could not be found with the user id \"" + id + "\""));

    workExperienceRepository.delete(workExperience);
  }

}
