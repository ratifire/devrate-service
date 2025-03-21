package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.MasteryLevel;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.exception.SpecializationLinkedToInterviewException;
import com.ratifire.devrate.exception.SpecializationLinkedToInterviewRequestException;
import com.ratifire.devrate.exception.SpecializationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SpecializationRepository;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.util.JsonConverter;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * The service responsible for managing user`s Specialization.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpecializationService {

  @Value("${specialization.defaultSpecializationsPath}")
  private String defaultSpecializationsPath;

  private final UserService userService;
  private final MasteryService masteryService;
  private final SpecializationRepository specializationRepository;
  private final InterviewRepository interviewRepository;
  private final DataMapper<SpecializationDto, Specialization> specializationMapper;
  private final DataMapper<MasteryDto, Mastery> masteryMapper;
  private final InterviewRequestRepository interviewRequestRepository;
  private final UserContextProvider userContextProvider;

  /**
   * Finds a specialization by its ID.
   *
   * @param specializationId The ID of the specialization to find.
   * @return The specialization if found.
   * @throws SpecializationNotFoundException If the specialization with the given ID is not found.
   */
  public Specialization findById(long specializationId) {
    return specializationRepository.findById(specializationId)
        .orElseThrow(
            () -> new SpecializationNotFoundException(
                "The specialization not found with specializationId " + specializationId));
  }

  /**
   * Retrieves specializationDto by ID.
   *
   * @param id the ID of the specialization
   * @return the specialization as a DTO
   * @throws SpecializationNotFoundException if specialization is not found
   */
  public SpecializationDto getDtoById(long id) {
    return specializationMapper.toDto(findById(id));
  }

  /**
   * Retrieves specialization by user ID.
   *
   * @param userId the ID of the user
   * @return the user's specialization as a DTO
   */
  public List<SpecializationDto> getSpecializationsByUserId(long userId) {
    User user = userService.findById(userId);
    return specializationMapper.toDto(user.getSpecializations());
  }

  /**
   * Retrieves main mastery by specializationId.
   *
   * @param id the ID of the specialization
   * @return the mastery as a DTO
   * @throws ResourceNotFoundException if mastery is not found
   */
  public MasteryDto getMainMasteryById(long id) {
    return Optional.ofNullable(findById(id).getMainMastery())
        .map(masteryMapper::toDto)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Main mastery not found for specialization with id: " + id));
  }

  /**
   * Retrieves Mastery by specialization ID.
   *
   * @param specializationId the ID of the specialization
   * @return the specialization's mastery as a DTO
   */
  public List<MasteryDto> getMasteriesBySpecializationId(long specializationId) {
    return Optional.ofNullable(findById(specializationId))
        .map(Specialization::getMasteries)
        .map(masteryMapper::toDto)
        .orElse(List.of());
  }

  /**
   * Creates specialization information. Сreate masteries for specialization.
   *
   * @param specializationDto the user's specialization information as a DTO
   * @return the created user specialization information as a DTO
   */
  @Transactional
  public SpecializationDto createSpecialization(SpecializationDto specializationDto,
      long userId) {
    validateBeforeCreate(specializationDto, userId);
    User user = userService.findById(userId);
    Specialization specialization = specializationMapper.toEntity(specializationDto);
    specialization.setUser(user);

    if (CollectionUtils.isEmpty(user.getSpecializations())) {
      specialization.setMain(true);
    }

    user.getSpecializations().add(specialization);
    userService.updateByEntity(user);
    createMasteriesForSpecialization(specialization,
        specializationDto.getMainMasteryLevel());
    return specializationMapper.toDto(specialization);
  }

  /**
   * Updates Specialization information.
   *
   * @param specializationDto the updated Specialization as a DTO
   * @return the updated Specialization as a DTO
   */
  @Transactional
  public SpecializationDto update(SpecializationDto specializationDto) {
    Specialization specialisation = findById(specializationDto.getId());
    if (specializationRepository.existsSpecializationByUserIdAndName(
        specialisation.getUser().getId(), specializationDto.getName())) {
      throw new ResourceAlreadyExistException("Specialization name is already exist.");
    }

    checkSpecializationByInterviewRequest(specialisation.getMainMastery().getId(),
        specializationDto.getId());

    boolean mainStatus = specialisation.isMain();
    specializationMapper.updateEntity(specializationDto, specialisation);
    specialisation.setMain(mainStatus);
    Specialization updatedSpecialization = specializationRepository.save(specialisation);
    return specializationMapper.toDto(updatedSpecialization);
  }

  private void checkSpecializationByInterviewRequest(Long mainMasteryId, Long specializationId) {
    long userId = userContextProvider.getAuthenticatedUserId();
    List<InterviewRequest> masteryRequests = interviewRequestRepository
        .findAllByMastery_IdAndUser_Id(mainMasteryId, userId);
    if (!masteryRequests.isEmpty()) {
      throw new SpecializationLinkedToInterviewRequestException(specializationId,
          masteryRequests.getFirst().getId());
    }
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
    List<String> defaultSpecializationNames = JsonConverter.loadStringFromJson(
        defaultSpecializationsPath);
    if (specializationName != null && !defaultSpecializationNames.contains(specializationName)) {
      throw new ResourceNotFoundException(
          "The specialization name \"" + specializationName + "\" not found.");
    }

    int masteryLevel = specializationDto.getMainMasteryLevel();
    if (!MasteryLevel.containsLevel(masteryLevel)) {
      throw new ResourceNotFoundException("The mastery level \"" + masteryLevel + "\" not found.");
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
    Specialization newMainSpecialization = findById(specializationId);

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
   * @throws SpecializationLinkedToInterviewException        if specialization stills linked to
   *                                                         interview
   * @throws SpecializationLinkedToInterviewRequestException if specialization stills linked to
   *                                                         interview request
   */
  @Transactional
  public void delete(long id) {
    long userId = userContextProvider.getAuthenticatedUserId();
    Specialization specialization = findById(id);
    Long mainMasteryId = specialization.getMainMastery().getId();

    List<Interview> existingInterviews = interviewRepository
        .findByMasteryIdAndUserId(mainMasteryId, userId);
    if (!existingInterviews.isEmpty()) {
      log.warn("Cannot delete specialization with ID: {} because it has linked interview ID: {}",
          id, existingInterviews.getFirst().getId());
      throw new SpecializationLinkedToInterviewException(id, existingInterviews.getFirst().getId());
    }

    List<InterviewRequest> existsInterviewRequests = interviewRequestRepository
        .findAllByMastery_IdAndUser_Id(mainMasteryId, userId);
    if (!existsInterviewRequests.isEmpty()) {
      log.warn("Cannot delete specialization with ID: {} because it has linked interview request "
              + "ID: {}", id, existsInterviewRequests.getFirst().getId());
      throw new SpecializationLinkedToInterviewRequestException(id,
          existsInterviewRequests.getFirst().getId());
    }

    specializationRepository.delete(specialization);
  }

  /**
   * Creates Masteries for specialization and softSkills for Masteries.
   */
  @Transactional
  public void createMasteriesForSpecialization(Specialization specialization,
      int mainMasteryLevel) {
    List<Mastery> masteries = createMasteryList(specialization);
    masteries.stream()
        .filter(mastery -> mastery.getLevel() == mainMasteryLevel)
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
    return Stream.of(MasteryLevel.values())
        .map(level -> Mastery.builder()
            .level(level.getLevel())
            .softSkillMark(BigDecimal.ZERO)
            .hardSkillMark(BigDecimal.ZERO)
            .specialization(specialization)
            .build())
        .toList();
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
   * @throws ResourceNotFoundException                       if the mastery does not belong to the
   *                                                         specified specialization
   * @throws SpecializationLinkedToInterviewRequestException if the specialization has existing
   *                                                         interview request
   */
  public MasteryDto setMainMasteryById(long specId, long masteryId) {
    Mastery newMainMastery = masteryService.getMasteryById(masteryId);
    Specialization specialization = findById(specId);
    if (!specialization.getMasteries().contains(newMainMastery)) {
      throw new ResourceNotFoundException(
          "The mastery does not belong to the specified specialization.");
    }

    checkSpecializationByInterviewRequest(specialization.getMainMastery().getId(), specId);

    specialization.setMainMastery(newMainMastery);
    specializationRepository.save(specialization);
    return masteryMapper.toDto(newMainMastery);
  }
}