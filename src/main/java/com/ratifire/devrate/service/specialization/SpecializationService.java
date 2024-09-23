package com.ratifire.devrate.service.specialization;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.exception.SpecializationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SpecializationRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
  private final Map<Integer, String> defaultMasteryLevels;
  private final List<String> defaultSpecializationNames;

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
    return masteryMapper.toDto(mainMastery);
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
   * Validates the {@link SpecializationDto} object before creating a specialization for a user.
   *
   * @param specializationDto the specialization data transfer object containing details for
   *                          validation
   * @param userId            the ID of the user for whom the specialization is being created
   * @throws ResourceNotFoundException     if the specialization name or main mastery name is not
   *                                       found
   * @throws ResourceAlreadyExistException if the user already has a main specialization or a
   *                                       specialization with the same name
   */
  public void validateBeforeCreate(SpecializationDto specializationDto,
      long userId) {
    String specializationName = specializationDto.getName();
    if (specializationName != null && !defaultSpecializationNames.contains(specializationName)) {
      throw new ResourceNotFoundException(
          "The specialization name \"" + specializationName + "\" not found.");
    }

    String masteryName = specializationDto.getMainMasteryName();
    if (masteryName != null && !defaultMasteryLevels.containsValue(masteryName)) {
      throw new ResourceNotFoundException("The mastery level \"" + masteryName + "\" not found.");
    }

    if (specializationDto.isMain()
        && specializationRepository.existsSpecializationByUserIdAndMainTrue(userId)) {
      throw new ResourceAlreadyExistException("Main level is already exist.");
    }

    if (specializationRepository.existsSpecializationByUserIdAndName(userId, specializationName)) {
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
   * Creates Masteries for specialization and softSkills for Masteries.
   */
  @Transactional
  public void createMasteriesForSpecialization(Specialization specialization,
      @NotNull String mainMasteryName) {
    if (mainMasteryName == null || mainMasteryName.isEmpty() || mainMasteryName.isBlank()) {
      throw new ResourceNotFoundException("The main mastery name is required param.");
    }

    List<Mastery> masteries = createMasteryList(specialization);
    masteries.stream()
        .filter(mastery -> defaultMasteryLevels.get(mastery.getLevel())
            .equalsIgnoreCase(mainMasteryName))
        .findFirst()
        .ifPresent(specialization::setMainMastery);

    specialization.setMasteries(masteries);
    specializationRepository.save(specialization);
    createSkillsForMasteries(masteries);
  }

  /**
   * Generates a list of mastery entities with default values for soft skill mark and hard skill
   * mark, covering all mastery levels available.
   *
   * @param specialization the specialization to associate with each mastery entity
   */
  private List<Mastery> createMasteryList(Specialization specialization) {
    return defaultMasteryLevels.keySet().stream()
        .map(s -> Mastery.builder()
            .level(s)
            .softSkillMark(BigDecimal.ZERO)
            .hardSkillMark(BigDecimal.ZERO)
            .specialization(specialization)
            .build())
        .collect(Collectors.toList());
  }

  /**
   * Creates default skills for all masteries within a given specialization.
   */
  private void createSkillsForMasteries(List<Mastery> masteryList) {
    for (Mastery mastery : masteryList) {
      masteryService.setSkillsForMastery(mastery);
    }
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

  /**
   * Updates the interview counts for the interviewer and candidate specializations after the
   * interview has been completed and conducted.
   *
   * @param interview the interview from which the interviewer and candidate specializations are
   *                  derived
   */
  public void updateUserInterviewCounts(Interview interview) {
    Specialization interviewer = interview.getInterviewerRequest().getMastery().getSpecialization();
    Specialization candidate = interview.getCandidateRequest().getMastery().getSpecialization();

    interviewer.setConductedInterviews(interviewer.getConductedInterviews() + 1);
    candidate.setCompletedInterviews(candidate.getCompletedInterviews() + 1);

    specializationRepository.saveAll(List.of(interviewer, candidate));
  }
}
