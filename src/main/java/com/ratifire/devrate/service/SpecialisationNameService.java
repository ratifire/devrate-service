package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.SpecialisationNameDto;
import com.ratifire.devrate.entity.SpecialisationName;
import com.ratifire.devrate.exception.SpecialisationNameNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SpecialisationNameRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing Specialisations.
 */
@Service
@RequiredArgsConstructor
public class SpecialisationNameService {

  private final SpecialisationNameRepository specialisationNameRepository;
  private final DataMapper<SpecialisationNameDto, SpecialisationName> dataMapper;

  /**
   * Retrieves Specialisation by ID.
   *
   * @param id the ID of the Specialisation
   * @return the Specialisation as a DTO
   * @throws SpecialisationNameNotFoundException if Specialisation is not found
   */
  public SpecialisationNameDto findById(long id) {
    return specialisationNameRepository.findById(id).map(dataMapper::toDto)
        .orElseThrow(
            () -> new SpecialisationNameNotFoundException("Specialisation not found with id: "
                + id));
  }

  /**
   * Retrieves Specialisation.
   *
   * @return the lists of Specialisations.
   */
  public List<SpecialisationNameDto> getAllSpecialisations() {
    List<SpecialisationName> specialisationNames = specialisationNameRepository.findAll();
    return specialisationNames.stream()
        .map(dataMapper::toDto)
        .collect(Collectors.toList());
  }
}
