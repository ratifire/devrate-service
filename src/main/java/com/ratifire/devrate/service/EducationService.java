package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.exception.EducationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for performing operations related to Education entities.
 */
@Service
@RequiredArgsConstructor
public class EducationService {

  private final EducationRepository repository;

  private final DataMapper<EducationDto, Education> mapper;

  /**
   * Retrieves an education entity by its ID.
   *
   * @param id The ID of the education entity to retrieve.
   * @return EducationDto representing the education entity with the specified ID.
   * @throws EducationNotFoundException If no education entity is found with the given ID.
   */
  public EducationDto getById(long id) {
    return repository.findById(id).map(mapper::toDto)
        .orElseThrow(() -> new EducationNotFoundException(id));
  }

  /**
   * Updates an existing education entity.
   *
   * @param id           The ID of the education entity to be updated.
   * @param educationDto The updated data for the education entity.
   * @return EducationDto representing the updated education entity.
   * @throws EducationNotFoundException If no education entity is found with the given ID.
   */
  public EducationDto update(long id, EducationDto educationDto) {
    Education education = repository.findById(id)
        .orElseThrow(() -> new EducationNotFoundException(id));

    mapper.updateEntity(educationDto, education);

    return mapper.toDto(repository.save(education));
  }

  /**
   * Deletes an education entity by its ID.
   *
   * @param id The ID of the education entity to be deleted.
   */
  public void delete(long id) {
    repository.deleteById(id);
  }
}
