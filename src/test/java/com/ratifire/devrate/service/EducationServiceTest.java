package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.exception.EducationNotFoundException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.EducationMapper;
import com.ratifire.devrate.repository.EducationRepository;
import com.ratifire.devrate.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the EducationService class.
 */
@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {

  @Mock
  private EducationRepository educationRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private EducationMapper educationMapper;

  @InjectMocks
  private EducationService educationService;

  private EducationDto educationDto;

  private Education education;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void before() {

    educationDto = EducationDto.builder()
        .id(1)
        .type("Course")
        .name("Hillel")
        .description("I learned a lot of knowledge")
        .startYear(2013)
        .endYear(2013)
        .build();

    education = Education.builder()
        .id(1)
        .type("Course")
        .name("Hillel")
        .description("I learned a lot of knowledge")
        .startYear(2013)
        .endYear(2013)
        .userId(1)
        .build();
  }

  @Test
  public void getByIdTest() {
    when(educationRepository.findById(anyLong())).thenReturn(Optional.ofNullable(education));
    when(educationMapper.toDto(education)).thenReturn(educationDto);

    EducationDto result = educationService.getById(anyLong());

    assertEquals(educationDto, result);
  }

  @Test
  public void getByWithUnExpectedIdTest() {
    when(educationRepository.findById(anyLong())).thenThrow(EducationNotFoundException.class);
    assertThrows(EducationNotFoundException.class, () -> educationService.getById(anyLong()));
  }

  @Test
  public void crateTest() {
    when(userRepository.existsById(anyLong())).thenReturn(true);
    when(educationRepository.save(any())).thenReturn(education);
    when(educationMapper.toEntity(any())).thenReturn(education);
    when(educationMapper.toDto(education)).thenReturn(educationDto);

    EducationDto result = educationService.create(anyLong(), educationDto);

    assertEquals(educationDto, result);
  }

  @Test
  public void crateWithUnExpectedUserIdTest() {
    when(userRepository.existsById(anyLong())).thenThrow(UserNotFoundException.class);
    assertThrows(UserNotFoundException.class,
        () -> educationService.create(anyLong(), educationDto));
  }

  @Test
  public void updateTest() {
    when(educationRepository.findById(anyLong())).thenReturn(Optional.ofNullable(education));
    when(educationRepository.save(any())).thenReturn(education);
    doNothing().when(educationMapper).toUpdate(any(), any());
    when(educationMapper.toDto(education)).thenReturn(educationDto);

    EducationDto result = educationService.update(anyLong(), educationDto);

    assertEquals(educationDto, result);
  }

  @Test
  public void updateWithUnExpectedIdTest() {
    when(educationRepository.findById(anyLong())).thenThrow(EducationNotFoundException.class);
    assertThrows(EducationNotFoundException.class, () -> educationService.getById(anyLong()));
  }

  @Test
  public void deleteTest() {
    doNothing().when(educationRepository).deleteById(anyLong());
    educationService.delete(anyInt());
    verify(educationRepository, times(1)).deleteById(anyLong());
  }
}
