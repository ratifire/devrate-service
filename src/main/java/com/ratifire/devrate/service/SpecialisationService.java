package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.SpecialisationDto;
import com.ratifire.devrate.entity.Specialisation;
import com.ratifire.devrate.exception.SpecialisationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SpecialisationRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user`s Specialisation.
 */
@Service
@RequiredArgsConstructor
public class SpecialisationService {

  private final SpecialisationRepository specialisationRepository;
  private final DataMapper<SpecialisationDto, Specialisation> specialisationDataMapper;

  /**
   * Retrieves Specialisation by ID.
   *
   * @param id the ID of the Specialisation
   * @return the user's Specialisation as a DTO
   * @throws SpecialisationNotFoundException if Specialisation information is not found
   */
  public SpecialisationDto findById(long id) {
    return specialisationRepository.findById(id).map(specialisationDataMapper::toDto)
        .orElseThrow(
            () -> new SpecialisationNotFoundException("Specialisation not found with id: "
                + id));
  }

  /**
   * Updates user Specialisation information.
   *
   * @param specialisationDto the updated user's Specialisation information as a DTO
   * @return the updated user Specialisation information as a DTO
   * @throws SpecialisationNotFoundException if the user Specialisation info does not exist by id
   */
  public SpecialisationDto update(SpecialisationDto specialisationDto) {
    long id = specialisationDto.getId();
    Optional<Specialisation> optionalSpecialisation = specialisationRepository.findById(id);

    return optionalSpecialisation.map(specialisation -> {
      specialisationDataMapper.updateEntity(specialisationDto, specialisation);
      Specialisation updatedSpecialisation = specialisationRepository.save(specialisation);
      return specialisationDataMapper.toDto(updatedSpecialisation);
    }).orElseThrow(() -> new SpecialisationNotFoundException("Specialisation`s id: " + id));
  }

  /**
   * Deletes Specialisation information by Specialisation ID.
   *
   * @param id the ID of the Specialisation whose Specialisation information is to be deleted
   */
  public void deleteById(long id) {
    specialisationRepository.deleteById(id);
  }

}
