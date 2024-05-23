package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.NicheDto;
import com.ratifire.devrate.dto.NicheLevelDto;
import com.ratifire.devrate.entity.Niche;
import com.ratifire.devrate.entity.NicheLevel;
import com.ratifire.devrate.entity.NicheLevelHistory;
import com.ratifire.devrate.enums.NicheLevelName;
import com.ratifire.devrate.exception.NicheNotFoundException;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NicheLevelHistoryRepository;
import com.ratifire.devrate.repository.NicheRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user`s niche.
 */
@Service
@RequiredArgsConstructor
public class NicheService {

  private final NicheRepository nicheRepository;
  private final DataMapper<NicheDto, Niche> nicheDataMapper;
  private final DataMapper<NicheLevelDto, NicheLevel> nicheLevelDataMapper;
  private final NicheLevelHistoryService nicheLevelHistoryService;
  private final NicheLevelService nicheLevelService;

  /**
   * Retrieves Niche by ID.
   *
   * @param id the ID of the Niche
   * @return the Niche as a DTO
   * @throws NicheNotFoundException if Niche is not found
   */
  public NicheDto findById(long id) {
    return nicheRepository.findById(id).map(nicheDataMapper::toDto)
        .orElseThrow(
            () -> new NicheNotFoundException("Niche not found with id: "
                + id));
  }

  /**
   * Updates niche information.
   *
   * @param nicheDto the updated niche as a DTO
   * @return the updated niche as a DTO
   * @throws NicheNotFoundException if the niche does not exist by id
   */
  public NicheDto update(NicheDto nicheDto, long id) {
    Niche specialisation = findNicheById(id);
    nicheDataMapper.updateEntity(nicheDto, specialisation);

    Niche updatedNiche = nicheRepository.save(specialisation);
    return nicheDataMapper.toDto(updatedNiche);
  }

  /**
   * Deletes niche by niche ID.
   *
   * @param id the ID of the niche whose is to be deleted
   */
  public void deleteById(long id) {
    nicheRepository.deleteById(id);
  }

  /**
   * Creates a new niche level for the specified specialization.
   *
   * @param nicheLevelDto The DTO containing information about the new niche level to be created.
   * @param nicheId       The ID of the niche for which the new niche level is created.
   * @return The newly created niche level entity.
   * @throws ResourceAlreadyExistException If a niche level with the same name already exists for
   *                                       the specified specialization.
   */
  public NicheLevel createLevel(NicheLevelDto nicheLevelDto, long nicheId) {
    validationExistLevelName(nicheId, nicheLevelDto.getNicheLevelName());
    Niche niche = findNicheById(nicheId);
    NicheLevel nicheLevel = nicheLevelDataMapper.toEntity(nicheLevelDto);
    niche.setLevel(nicheLevel);
    update(nicheDataMapper.toDto(niche), nicheId);
    nicheLevelHistoryService.saveNicheLevelHistory(nicheId, nicheLevel.getId());
    return nicheLevel;
  }

  /**
   * Validates if a niche level with the specified name exists for the given niche.
   *
   * @param nicheId        The ID of the niche.
   * @param nicheLevelName The name of the niche level.
   * @throws ResourceAlreadyExistException If the niche level already exists for the niche.
   */
  private void validationExistLevelName(long nicheId, NicheLevelName nicheLevelName) {
    List<NicheLevelHistory> historyList = nicheLevelHistoryService.findByIdNicheId(nicheId);
    for (NicheLevelHistory nicheLevelHistory : historyList) {
      Long nicheLevelId = nicheLevelHistory.getId().getNicheLevelId();
      NicheLevel existLevelName = nicheLevelService.findNicheLevelById(nicheLevelId);
      if (nicheLevelName.equals(existLevelName.getNicheLevelName())) {
        throw new ResourceAlreadyExistException("This level already exists in this niche.");
      }
    }
  }


  /**
   * Finds a niche by its ID.
   *
   * @param nicheId The ID of the niche to find.
   * @return The niche if found.
   * @throws NicheNotFoundException If the niche with the given ID is not found.
   */
  private Niche findNicheById(long nicheId) {
    return nicheRepository.findById(nicheId)
        .orElseThrow(
            () -> new NicheNotFoundException("The niche not found with nicheId " + nicheId));
  }

  /**
   * Retrieves the niche level by the ID of the niche.
   *
   * @param nicheId The ID of the niche.
   * @return The DTO representation of the niche level.
   */
  public NicheLevelDto getNicheLevelByNicheId(long nicheId) {
    NicheLevel nicheLevel = findNicheById(nicheId).getLevel();
    return nicheLevelDataMapper.toDto(nicheLevel);
  }

  /**
   * Retrieves the history of niche levels by the ID of the niche.
   *
   * @param nicheId The ID of the niche.
   * @return A list of DTO representations of niche levels.
   */
  public List<NicheLevelDto> getNicheLevelHistoryByNicheId(long nicheId) {
    List<NicheLevelHistory> historyList = nicheLevelHistoryService.findByIdNicheId(nicheId);
    return historyList.stream()
        .map(history -> {
          Long nicheLevelId = history.getId().getNicheLevelId();
          return nicheLevelService.findNicheLevelById(nicheLevelId);
        })
        .map(nicheLevelDataMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Updates main niche level.
   *
   * @return the updated niche level as a DTO
   */
  public NicheLevelDto updateNicheLevelIdAtNiche(long nicheId, long levelId) {
    NicheLevel nicheLevel = nicheLevelService.findNicheLevelById(levelId);
    Niche niche = findNicheById(nicheId);
    niche.setLevel(nicheLevel);
    Niche updatedNiche = nicheRepository.save(niche);
    return nicheLevelDataMapper.toDto(updatedNiche.getLevel());
  }

  /**
   * Checks if any niche DTO in the list represents a main niche level.
   *
   * @param nicheDtoList the list of niche DTOs to check
   * @return true if any niche DTO in the list represents a main niche level, otherwise false
   */
  private boolean isMainNicheExist(List<NicheDto> nicheDtoList) {
    return nicheDtoList.stream().anyMatch(NicheDto::isMain);
  }

  /**
   * Checks if any niche DTO in the list has the specified name.
   *
   * @param name         the name to check
   * @param nicheDtoList the list of niche DTOs to search
   * @return true if any niche DTO in the list has the specified name, otherwise false
   */
  private boolean isNicheNameAlreadyExist(String name, List<NicheDto> nicheDtoList) {
    return nicheDtoList.stream()
        .anyMatch(nicheDto -> nicheDto.getName().equals(name));
  }

  /**
   * Checks if the provided niche DTO represents a main niche level and if the niche name already
   * exists in the provided list of niche DTOs.
   *
   * @param nicheDto     the niche DTO to check
   * @param nicheDtoList the list of niche DTOs to search
   * @throws ResourceAlreadyExistException if a main niche level already exists or if the niche name
   *                                       is already taken
   */
  public void checkIsMainAndNicheNameAlreadyExist(NicheDto nicheDto, List<NicheDto> nicheDtoList) {
    if (nicheDto.isMain() && isMainNicheExist(nicheDtoList)) {
      throw new ResourceAlreadyExistException("Main level is already exist.");
    }
    if (isNicheNameAlreadyExist(nicheDto.getName(), nicheDtoList)) {
      throw new ResourceAlreadyExistException("Niche name is already exist.");
    }
  }

  /**
   * Changes the main niche status from an old main niche to a new main niche.
   *
   * @param oldMainNicheId the ID of the current main niche
   * @param newMainNicheId the ID of the niche that will become the new main niche
   * @return the updated new main niche as a DTO
   */
  public NicheDto changeMainNicheStatus(long oldMainNicheId,
      long newMainNicheId) {
    Niche oldMainNiche = findNicheById(oldMainNicheId);
    oldMainNiche.setMain(false);

    Niche newMainNiche = findNicheById(newMainNicheId);
    newMainNiche.setMain(true);

    nicheRepository.saveAll(Arrays.asList(oldMainNiche, newMainNiche));
    return nicheDataMapper.toDto(newMainNiche);
  }

}
