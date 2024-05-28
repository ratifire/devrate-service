package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.LanguageProficiencyDataDto;
import com.ratifire.devrate.enums.LanguageProficiencyName;
import com.ratifire.devrate.mapper.LanguageProficiencyDataMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the LanguageProficiencyDataService class.
 */
@ExtendWith(MockitoExtension.class)
class LanguageProficiencyDataServiceTest {

  @InjectMocks
  private LanguageProficiencyDataService languageProficiencyDataService;

  @Mock
  private LanguageProficiencyDataMapper mapper;

  List<LanguageProficiencyDataDto> languageProficiencyDataDto;
  LanguageProficiencyName[] languageProficiencyNames;

  @BeforeEach
  public void init() {
    languageProficiencyNames = LanguageProficiencyName.values();

    languageProficiencyDataDto = Arrays.asList(
        new LanguageProficiencyDataDto("English", "EN",
            Arrays.asList("Beginner", "Pre-Intermediate", "Intermediate", "Upper-Intermediate",
                "Advanced", "Proficient")),
        new LanguageProficiencyDataDto("Spanish", "ES",
            Arrays.asList("Principiante", "Pre-intermedio", "Intermedio", "ntermedio avanzado",
                "Avanzado", "Fluido"))
    );
  }

  @Test
  void getAllLanguageProficiencyDataTest() {
    when(mapper.toDto(Arrays.asList(languageProficiencyNames))).thenReturn(
        languageProficiencyDataDto);

    List<LanguageProficiencyDataDto> result = languageProficiencyDataService
        .getAllLanguageProficiencyData();
    assertEquals(languageProficiencyDataDto, result);

  }

}
