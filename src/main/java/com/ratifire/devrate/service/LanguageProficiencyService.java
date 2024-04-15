package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.entity.LanguageProficiency;
import com.ratifire.devrate.exception.LanguageProficiencyNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.LanguageProficiencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing languages proficiencies of users. This class handles the business
 * logic for retrieving, updating, and deleting languages proficiencies associated with users.
 */
@Service
@RequiredArgsConstructor
public class LanguageProficiencyService {

  private final LanguageProficiencyRepository languageProficiencyRepository;

  private final DataMapper<LanguageProficiencyDto, LanguageProficiency> languageProficiencyMapper;

  /**
   * Retrieves a language proficiency by its ID and converts it to a LanguageProficiencyDto.
   *
   * @param id the ID of the language proficiency to retrieve
   * @return the LanguageProficiencyDto corresponding to the specified ID
   * @throws LanguageProficiencyNotFoundException if no language proficiency is found for the given
   *                                              ID
   */
  public LanguageProficiencyDto findById(long id) {
    return languageProficiencyMapper.toDto(languageProficiencyById(id));
  }

  /**
   * Updates an existing language proficiency identified by ID with new information provided in the
   * languageProficiencyDto.
   *
   * @param id                     the ID of the language proficiency to update
   * @param languageProficiencyDto the new language proficiency information
   * @return the updated LanguageProficiencyDto
   * @throws LanguageProficiencyNotFoundException if no language proficiency is found for the given
   *                                              ID
   */
  public LanguageProficiencyDto update(long id, LanguageProficiencyDto languageProficiencyDto) {
    LanguageProficiency languageProficiency = languageProficiencyById(id);
    languageProficiencyMapper.updateEntity(languageProficiencyDto, languageProficiency);
    languageProficiencyRepository.save(languageProficiency);
    return languageProficiencyMapper.toDto(languageProficiency);
  }

  /**
   * Deletes a language proficiency by its ID.
   *
   * @param id the ID of the language proficiency to delete
   */
  public void delete(long id) {
    languageProficiencyRepository.deleteById(id);
  }

  /**
   * Retrieves a language proficiency by ID.
   *
   * @param id the ID of the language proficiency to retrieve
   * @return the language proficiency entity
   * @throws LanguageProficiencyNotFoundException if the language proficiency with the specified ID
   *                                              is not found
   */
  private LanguageProficiency languageProficiencyById(long id) {
    return languageProficiencyRepository.findById(id)
        .orElseThrow(() -> new LanguageProficiencyNotFoundException(id));
  }

}