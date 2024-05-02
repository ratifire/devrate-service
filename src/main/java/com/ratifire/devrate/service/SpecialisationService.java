package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.SpecialisationDto;
import com.ratifire.devrate.entity.Specialisation;
import com.ratifire.devrate.exception.EmploymentRecordNotFoundException;
import com.ratifire.devrate.exception.SpecialisationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SpecialisationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing Specialisations.
 */
@Service
@RequiredArgsConstructor
public class SpecialisationService {

  private final SpecialisationRepository specialisationRepository;
  private final DataMapper<SpecialisationDto, Specialisation> dataMapper;

  /**
   * Retrieves Specialisation by ID.
   *
   * @param id the ID of the Specialisation
   * @return the Specialisation as a DTO
   * @throws SpecialisationNotFoundException if Specialisation is not found
   */
  public SpecialisationDto findById(long id) {
    return specialisationRepository.findById(id).map(dataMapper::toDto)
        .orElseThrow(
            () -> new SpecialisationNotFoundException("Specialisation not found with id: "
                + id));
  }

  /**
   * Retrieves Specialisation.
   *
   * @return the lists of Specialisations.
   */
  public List<SpecialisationDto> getAllSpecialisations() {
    List<Specialisation> specialisations = specialisationRepository.findAll();
    return specialisations.stream()
        .map(dataMapper::toDto)
        .collect(Collectors.toList());
  }
}
