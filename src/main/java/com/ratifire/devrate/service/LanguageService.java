package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.LanguageDto;
import com.ratifire.devrate.entity.Language;
import com.ratifire.devrate.exception.LanguageNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing languages of users. This class handles the business logic for
 * creating, retrieving, updating, and deleting languages associated with users.
 */
@Service
@RequiredArgsConstructor
public class LanguageService {

  private final LanguageRepository languageRepository;

  private final DataMapper<LanguageDto, Language> languageMapper;

  /**
   * Retrieves a language by its ID and converts it to a LanguageDto. If the language is not found,
   * a LanguageNotFoundException is thrown.
   *
   * @param id the ID of the language to retrieve
   * @return the LanguageDto corresponding to the specified ID
   * @throws LanguageNotFoundException if no language is found for the given ID
   */
  public LanguageDto findById(long id) {
    return languageRepository.findById(id)
        .map(languageMapper::toDto)
        .orElseThrow(() -> new LanguageNotFoundException(id));
  }

  /**
   * Creates a new language for a user identified by userId.
   *
   * @param userId      the ID of the user to whom the language belongs
   * @param languageDto the language information to create
   * @return the created LanguageDto
   */
  public LanguageDto create(long userId, LanguageDto languageDto) {
    Language language = languageMapper.toEntity(languageDto);
    language.setUserId(userId);
    languageRepository.save(language);
    return languageMapper.toDto(language);
  }

  /**
   * Updates an existing language identified by ID with new information provided in the
   * languageDto.
   *
   * @param id          the ID of the language to update
   * @param languageDto the new language information
   * @return the updated LanguageDto
   * @throws LanguageNotFoundException if no language is found for the given ID
   */
  public LanguageDto update(long id, LanguageDto languageDto) {
    Language language = languageRepository.findById(id)
        .orElseThrow(() -> new LanguageNotFoundException(id));
    languageMapper.updateEntity(languageDto, language);
    languageRepository.save(language);
    return languageMapper.toDto(language);
  }

  /**
   * Deletes a language by its ID.
   *
   * @param id the ID of the language to delete
   */
  public void delete(long id) {
    if (!languageRepository.existsById(id)) {
      throw new LanguageNotFoundException(id);
    }
    languageRepository.deleteById(id);
  }

}