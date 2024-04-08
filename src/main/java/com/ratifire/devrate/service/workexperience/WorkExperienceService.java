package com.ratifire.devrate.service.workexperience;

import com.ratifire.devrate.dto.WorkExperienceDto;
import com.ratifire.devrate.entity.WorkExperience;
import com.ratifire.devrate.exception.WorkExperienceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.WorkExperienceRepository;
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
public class WorkExperienceService {

  private final WorkExperienceRepository workExperienceRepository;
  private final DataMapper<WorkExperienceDto, WorkExperience> workExperienceDataMapper;

  /**
   * Retrieves work experience information by user ID.
   *
   * @param userId the ID of the user
   * @return the user's work experience as a DTO
   * @throws WorkExperienceNotFoundException if work experience information is not found
   */
  public List<WorkExperienceDto> findByUserId(long userId) {
    List<WorkExperience> workExperiences = workExperienceRepository.findByUserId(userId);
    if (workExperiences.isEmpty()) {
      throw new WorkExperienceNotFoundException("The user's work experience information "
          + "could not be found with the user userId \"" + userId + "\"");
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
   */
  public WorkExperienceDto create(WorkExperienceDto workExperienceDto) {
    long userId = workExperienceDto.getUserId();

    WorkExperience workExperience = workExperienceDataMapper.toEntity(workExperienceDto);
    workExperience.setUserId(userId);

    return workExperienceDataMapper.toDto(workExperienceRepository.save(workExperience));
  }

  /**
   * Updates user work experience information.
   *
   * @param workExperienceDto the updated user's work experience information as a DTO
   * @return the updated user work experience information as a DTO
   * @throws WorkExperienceNotFoundException if the user work experience info does not exist by
   *                                             user id
   */
  public WorkExperienceDto update(WorkExperienceDto workExperienceDto) {
    long id = workExperienceDto.getId();
    WorkExperience workExperience = workExperienceRepository.findById(id)
        .orElseThrow(
            () -> new WorkExperienceNotFoundException("User Id" + id));

    workExperienceDataMapper.updateEntity(workExperienceDto, workExperience);

    return workExperienceDataMapper.toDto(workExperienceRepository.save(workExperience));
  }

  /**
   * Deletes user work experience information by user ID.
   *
   * @param id the ID of the user whose work experience information is to be deleted
   * @throws WorkExperienceNotFoundException if user work experience information is not found
   */
  public void delete(long id) {
    WorkExperience workExperience = workExperienceRepository.findById(id)
        .orElseThrow(
            () -> new WorkExperienceNotFoundException("id " + id));

    workExperienceRepository.delete(workExperience);
  }

}
