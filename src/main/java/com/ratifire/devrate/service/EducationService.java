package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.exception.EducationNotFoundException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.EducationMapper;
import com.ratifire.devrate.repository.EducationRepository;
import com.ratifire.devrate.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for performing operations related to Education entities.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationService {

  private final EducationRepository educationRepository;

  private final UserRepository userRepository;

  private final EducationMapper educationMapper;

  /**
   * Retrieves all education entities.
   *
   * @return List of EducationDto representing all education entities.
   */
  public List<EducationDto> getAll() {
    return educationRepository.findAll().stream().map(educationMapper::toDto).toList();
  }

  /**
   * Retrieves an education entity by its ID.
   *
   * @param id The ID of the education entity to retrieve.
   * @return EducationDto representing the education entity with the specified ID.
   * @throws EducationNotFoundException If no education entity is found with the given ID.
   */
  public EducationDto getById(long id) {
    return educationRepository.findById(id).map(educationMapper::toDto)
        .orElseThrow(() -> new EducationNotFoundException("Education not found with id: " + id));
  }

  /**
   * Creates a new education entity.
   *
   * @param userId       The ID of the user to whom the education belongs.
   * @param educationDto The data representing the education entity to be created.
   * @return EducationDto representing the newly created education entity.
   * @throws UserNotFoundException If no user is found with the given ID.
   */
  @Transactional
  public EducationDto create(long userId, EducationDto educationDto) {
    userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

    Education education = educationMapper.toEntity(educationDto);
    education.setUserId(userId);

    return educationMapper.toDto(educationRepository.save(education));
  }

  /**
   * Updates an existing education entity.
   *
   * @param id           The ID of the education entity to be updated.
   * @param educationDto The updated data for the education entity.
   * @return EducationDto representing the updated education entity.
   * @throws EducationNotFoundException If no education entity is found with the given ID.
   */
  @Transactional
  public EducationDto update(long id, EducationDto educationDto) {
    Education education = educationRepository.findById(id)
        .orElseThrow(() -> new EducationNotFoundException(
            "Education not found with id: " + id));

    educationMapper.toUpdate(educationDto, education);

    return educationMapper.toDto(educationRepository.save(education));
  }

  /**
   * Deletes an education entity by its ID.
   *
   * @param id The ID of the education entity to be deleted.
   */
  @Transactional
  public void delete(long id) {
    educationRepository.deleteById(id);
  }
}
