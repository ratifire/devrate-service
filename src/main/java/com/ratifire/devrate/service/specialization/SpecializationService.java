package com.ratifire.devrate.service.specialization;

import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.SpecializationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user`s Specialization.
 */
@Service
@RequiredArgsConstructor
public class SpecializationService {

  private final SpecializationRepository specializationRepository;
  private final DataMapper<SpecializationDto, Specialization> specializationMapper;

  /**
   * Retrieves specialization by ID.
   *
   * @param id the ID of the specialization
   * @return the specialization as a DTO
   * @throws SpecializationNotFoundException if specialization is not found
   */
  public SpecializationDto findById(long id) {
    return specializationRepository.findById(id).map(specializationMapper::toDto)
        .orElseThrow(
            () -> new SpecializationNotFoundException("Specialization not found with id: " + id));
  }

  /**
   * Updates Specialization information.
   *
   * @param specializationDto the updated Specialization as a DTO
   * @return the updated Specialization as a DTO
   */
  public SpecializationDto update(SpecializationDto specializationDto, long id) {
    Specialization specialisation = findSpecializationById(id);

    if (isSpecializationNameAlreadyExist(specializationDto.getName(),
        specialisation.getUser().getId())) {
      throw new ResourceAlreadyExistException("Specialization name is already exist.");
    }

    boolean mainStatus = specialisation.isMain();
    specializationMapper.updateEntity(specializationDto, specialisation);
    specialisation.setMain(mainStatus);
    Specialization updatedSpecialization = specializationRepository.save(specialisation);
    return specializationMapper.toDto(updatedSpecialization);
  }

  /**
   * Finds a specialization by its ID.
   *
   * @param specializationId The ID of the specialization to find.
   * @return The specialization if found.
   * @throws SpecializationNotFoundException If the specialization with the given ID is not found.
   */
  private Specialization findSpecializationById(long specializationId) {
    return specializationRepository.findById(specializationId)
        .orElseThrow(
            () -> new SpecializationNotFoundException(
                "The specialization not found with specializationId " + specializationId));
  }

  /**
   * Checks if any specialization DTO in the list represents a main specialization level.
   *
   * @param userId ID of user
   * @return true if any specializationDTO in the list represents a main level, otherwise false
   */
  private boolean isMainSpecializationExist(long userId) {
    return specializationRepository.getSpecializationsByUserId(userId).stream()
        .anyMatch(SpecializationDto::isMain);
  }

  /**
   * Checks if any specialization DTO in the list has the specified name.
   *
   * @param name   the name to check
   * @param userId ID of user
   * @return true if any specialization DTO in the list has the specified name, otherwise false
   */
  private boolean isSpecializationNameAlreadyExist(String name, long userId) {
    return specializationRepository.getSpecializationsByUserId(userId).stream()
        .anyMatch(specializationDto -> specializationDto.getName().equals(name));
  }

  /**
   * Checks if the provided specialization DTO represents a main specialization level and if the
   * specialization name already exists in the provided list of specialization DTOs.
   *
   * @param specializationDto the specialization DTO to check
   * @param userId            ID of user
   * @throws ResourceAlreadyExistException if a main specialization level already exists or if the
   *                                       specialization name is already taken
   */
  public void checkIsMainAndSpecializationNameAlreadyExist(SpecializationDto specializationDto,
      long userId) {
    if (specializationDto.isMain() && isMainSpecializationExist(userId)) {
      throw new ResourceAlreadyExistException("Main level is already exist.");
    }
    if (isSpecializationNameAlreadyExist(specializationDto.getName(), userId)) {
      throw new ResourceAlreadyExistException("Specialization name is already exist.");
    }
  }

  /**
   * Updates the main specialization status to the specified specialization ID. If there is an
   * existing main specialization, its status will be set to false.
   *
   * @param newMainSpecializationId the ID of the specialization that will become the new main
   *                                specialization
   * @return the updated new main specialization as a DTO
   */
  public SpecializationDto setMainSpecializationStatus(long newMainSpecializationId) {
    Specialization newMainSpecialization = findSpecializationById(newMainSpecializationId);

    specializationRepository.findSpecializationByUserIdAndMainTrue(
            newMainSpecialization.getUser().getId())
        .ifPresent(
            mainSpecialization -> {
              mainSpecialization.setMain(false);
              specializationRepository.save(mainSpecialization);
            });

    newMainSpecialization.setMain(true);

    specializationRepository.save(newMainSpecialization);
    return specializationMapper.toDto(newMainSpecialization);
  }

  /**
   * Deletes specialization by specialization ID.
   *
   * @param id the ID of the specialization whose is to be deleted
   */
  public void deleteById(long id) {
    specializationRepository.deleteById(id);
  }

}
