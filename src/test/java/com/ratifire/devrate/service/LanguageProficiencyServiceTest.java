package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.entity.LanguageProficiency;
import com.ratifire.devrate.exception.LanguageProficiencyNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.LanguageProficiencyRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LanguageProficiencyServiceTest {

  @InjectMocks
  private LanguageProficiencyService languageProficiencyService;

  @Mock
  private LanguageProficiencyRepository languageProficiencyRepository;

  @Mock
  private DataMapper<LanguageProficiencyDto, LanguageProficiency> languageProficiencyMapper;
  private final long languageProficiencyId = 1;
  private LanguageProficiencyDto languageProficiencyDto;
  private LanguageProficiency languageProficiency;

  @BeforeEach
  void setUp() {
    languageProficiencyDto = new LanguageProficiencyDto();
    languageProficiency = new LanguageProficiency();
  }

  @Test
  void findByIdTest() {
    when(languageProficiencyRepository.findById(any())).thenReturn(
        Optional.of(languageProficiency));
    when(languageProficiencyMapper.toDto(languageProficiency)).thenReturn(languageProficiencyDto);

    LanguageProficiencyDto result = languageProficiencyService.findById(languageProficiencyId);

    assertEquals(languageProficiencyDto, result);
  }

  @Test
  void findByIdThrowLanguageProficiencyNotFoundExceptionTest() {
    when(languageProficiencyRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(LanguageProficiencyNotFoundException.class,
        () -> languageProficiencyService.findById(languageProficiencyId));
  }

  @Test
  void updateTest() {
    when(languageProficiencyRepository.findById(any())).thenReturn(
        Optional.of(languageProficiency));
    when(languageProficiencyMapper.updateEntity(any(), any())).thenReturn(languageProficiency);
    when(languageProficiencyRepository.save(any(LanguageProficiency.class))).thenReturn(
        languageProficiency);
    when(languageProficiencyMapper.toDto(any(LanguageProficiency.class))).thenReturn(
        languageProficiencyDto);

    LanguageProficiencyDto languageProficiencyDtoUpdated = languageProficiencyService.update(
        languageProficiencyId,
        languageProficiencyDto);

    assertEquals(languageProficiencyDto, languageProficiencyDtoUpdated);
    verify(languageProficiencyRepository).save(any(LanguageProficiency.class));
  }


  @Test
  void updateThrowLanguageProficiencyNotFoundExceptionTest() {
    when(languageProficiencyRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(LanguageProficiencyNotFoundException.class,
        () -> languageProficiencyService.update(languageProficiencyId, languageProficiencyDto));
  }

  @Test
  void deleteTest() {
    languageProficiencyService.delete(languageProficiencyId);

    verify(languageProficiencyRepository).deleteById(languageProficiencyId);
  }
}