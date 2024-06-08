package com.ratifire.devrate.service.specialization;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.enums.MasteryLevel;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.exception.SpecializationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SpecializationRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  private final DataMapper<MasteryDto, Mastery> masteryMapper;
  private final MasteryService masteryService;

  /**
   * Retrieves specialization by ID.
   *
   * @param id the ID of the specialization
   * @return the specialization as a DTO
   * @throws SpecializationNotFoundException if specialization is not found
   */
  public SpecializationDto findById(long id) {
    return specializationMapper.toDto(findSpecializationById(id));
  }

  /**
   * Retrieves main mastery by specializationId.
   *
   * @param id the ID of the specialization
   * @return the mastery as a DTO
   * @throws ResourceNotFoundException if mastery is not found
   */
  public MasteryDto getMainMasteryById(long id) {
    Mastery mainMastery = findSpecializationById(id).getMainMastery();
    if (mainMastery == null) {
      throw new ResourceNotFoundException(
          "Main mastery not found for specialization with id: " + id);
    }
    return masteryMapper.toDto(findSpecializationById(id).getMainMastery());
  }

  /**
   * Updates Specialization information.
   *
   * @param specializationDto the updated Specialization as a DTO
   * @return the updated Specialization as a DTO
   */
  @Transactional
  public SpecializationDto update(SpecializationDto specializationDto) {
    Specialization specialisation = findSpecializationById(specializationDto.getId());
    if (specializationRepository.existsSpecializationByUserIdAndName(
        specialisation.getUser().getId(), specializationDto.getName())) {
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
    if (specializationDto.isMain()
        && specializationRepository.existsSpecializationByUserIdAndMainTrue(userId)) {
      throw new ResourceAlreadyExistException("Main level is already exist.");
    }
    if (specializationRepository.existsSpecializationByUserIdAndName(userId,
        specializationDto.getName())) {
      throw new ResourceAlreadyExistException("Specialization name is already exist.");
    }
  }

  /**
   * Updates the main specialization status to the specified specialization ID. If there is an
   * existing main specialization, its status will be set to false.
   *
   * @param specializationId the ID of the specialization that will become the new main
   *                         specialization
   * @return the updated new main specialization as a DTO
   */
  public SpecializationDto setAsMainById(long specializationId) {
    Specialization newMainSpecialization = findSpecializationById(specializationId);

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

  /**
   * Retrieves Mastery by specialization ID.
   *
   * @param specializationId the ID of the specialization
   * @return the specialization's mastery as a DTO
   */
  public List<MasteryDto> getMasteriesBySpecializationId(long specializationId) {
    Specialization specialization = findSpecializationById(specializationId);
    return masteryMapper.toDto(specialization.getMasteries());
  }

  /**
   * Creates Masteries for specialization.
   */
  public void createMasteriesForSpecialization(long specializationId) {
    Specialization specialization = findSpecializationById(specializationId);
    List<Mastery> masteryList = createMasteryList();
    specialization.setMasteries(masteryList);
    specializationRepository.save(specialization);
  }

  /**
   * Generates a list of mastery entities with default values for soft skill mark and hard skill
   * mark, covering all mastery levels available.
   */
  private List<Mastery> createMasteryList() {
    return Stream.of(MasteryLevel.values())
        .map(level -> Mastery.builder()
            .name(level)
            .softSkillMark(BigDecimal.ZERO)
            .hardSkillMark(BigDecimal.ZERO)
            .build())
        .collect(Collectors.toList());
  }

  /**
   * Sets the specified mastery as the main mastery for the given specialization. Verifies that the
   * mastery belongs to the specialization before updating.
   *
   * @param specId    the ID of the specialization to which the mastery should be set as main
   * @param masteryId the ID of the mastery that will become the new main mastery
   * @return the updated main mastery as a DTO
   * @throws ResourceNotFoundException if the mastery does not belong to the specified
   *                                   specialization
   */
  public MasteryDto setMainMasteryById(long specId, long masteryId) {
    Mastery newMainMastery = masteryService.getMasteryById(masteryId);
    Specialization specialization = findSpecializationById(specId);
    if (!specialization.getMasteries().contains(newMainMastery)) {
      throw new ResourceNotFoundException(
          "The mastery does not belong to the specified specialization.");
    }
    specialization.setMainMastery(newMainMastery);
    specializationRepository.save(specialization);
    return masteryMapper.toDto(newMainMastery);
  }
}
