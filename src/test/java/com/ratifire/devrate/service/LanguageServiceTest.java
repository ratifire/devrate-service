package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.LanguageDto;
import com.ratifire.devrate.entity.Language;
import com.ratifire.devrate.exception.LanguageNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.LanguageRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LanguageServiceTest {

  @InjectMocks
  private LanguageService languageService;

  @Mock
  private LanguageRepository languageRepository;

  @Mock
  private DataMapper<LanguageDto, Language> languageMapper;

  private LanguageDto languageDto;

  private Language language;

  private long userId = 1;

  private long id = 1;

  @BeforeEach
  void setUp() {
    languageDto = new LanguageDto();
    language = new Language();
  }

  @Test
  void findByIdTest() {
    when(languageRepository.findById(id)).thenReturn(Optional.of(language));
    when(languageMapper.toDto(language)).thenReturn(languageDto);

    LanguageDto result = languageService.findById(id);

    assertEquals(languageDto, result);
    verify(languageRepository).findById(id);
    verify(languageMapper).toDto(language);
  }

  @Test
  void findByIdThrowLanguageNotFoundExceptionTest() {
    when(languageRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(LanguageNotFoundException.class, () -> languageService.findById(id));
  }

  @Test
  void createTest() {
    when(languageMapper.toEntity(languageDto)).thenReturn(language);
    when(languageRepository.save(language)).thenReturn(language);
    when(languageMapper.toDto(language)).thenReturn(languageDto);

    LanguageDto languageDtoCreated = languageService.create(userId, languageDto);

    assertEquals(languageDto, languageDtoCreated);
    verify(languageMapper).toEntity(languageDto);
    verify(languageRepository).save(language);
    verify(languageMapper).toDto(language);
  }

  @Test
  void updateTest() {
    when(languageRepository.findById(id)).thenReturn(Optional.of(language));
    when(languageMapper.updateEntity(any(), any())).thenReturn(language);
    when(languageRepository.save(any(Language.class))).thenReturn(language);
    when(languageMapper.toDto(any(Language.class))).thenReturn(languageDto);

    LanguageDto languageDtoUpdated = languageService.update(id, languageDto);

    assertEquals(languageDto, languageDtoUpdated);
    verify(languageRepository).findById(id);
    verify(languageRepository).save(any(Language.class));
    verify(languageMapper).updateEntity(any(LanguageDto.class), any(Language.class));
    verify(languageMapper).toDto(any(Language.class));
  }


  @Test
  void updateThrowLanguageNotFoundExceptionTest() {
    when(languageRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(LanguageNotFoundException.class, () -> languageService.update(id, languageDto));
  }

  @Test
  void deleteTest() {
    when(languageRepository.existsById(id)).thenReturn(true);

    languageService.delete(id);

    verify(languageRepository).existsById(id);
    verify(languageRepository).deleteById(id);
  }

  @Test
  void deleteThrowLanguageNotFoundExceptionTest() {
    when(languageRepository.existsById(id)).thenReturn(false);

    assertThrows(LanguageNotFoundException.class, () -> languageService.delete(id));
  }

}