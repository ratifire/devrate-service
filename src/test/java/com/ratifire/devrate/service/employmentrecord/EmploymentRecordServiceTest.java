package com.ratifire.devrate.service.employmentrecord;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.exception.EmploymentRecordNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.EmploymentRecordRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
public class EmploymentRecordServiceTest {

  @Mock
  private EmploymentRecordRepository employmentRecordRepository;
  @Mock
  private DataMapper<EmploymentRecordDto, EmploymentRecord> employmentRecordMapper;
  @InjectMocks
  private EmploymentRecordService employmentRecordService;
  private EmploymentRecordDto employmentRecordDto;
  @Mock
  private EmploymentRecord employmentRecord;


  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void before() {
    employmentRecordDto = EmploymentRecordDto.builder()
        .id(1L)
        .startDate(LocalDate.ofEpochDay(2023 - 01 - 01))
        .endDate(LocalDate.ofEpochDay(2022 - 01 - 01))
        .position("Java Developer")
        .companyName("New Company")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("1", "2", "3"))
        .build();
  }

  @Test
  public void getByIdTest() {
    List<EmploymentRecord> employmentRecordArrayList = new ArrayList<>();
    employmentRecordArrayList.add(employmentRecord);
    when(employmentRecordRepository.findByUserId(8882L)).thenReturn(employmentRecordArrayList);

    List<EmploymentRecordDto> result = employmentRecordService.getEmploymentRecordsByUserId(8882L);
    assertNotNull(result);

    List<EmploymentRecordDto> expected = employmentRecordArrayList.stream()
        .map(employmentRecordMapper::toDto)
        .collect(Collectors.toList());

    assertEquals(expected, result);
  }

  @Test
  public void createEmploymentRecordTest() {
    when(employmentRecordMapper.toEntity(any(EmploymentRecordDto.class))).thenReturn(
        new EmploymentRecord());
    when(employmentRecordMapper.toDto(any(EmploymentRecord.class)))
        .thenReturn(employmentRecordDto);

    when(employmentRecordRepository.save(any(EmploymentRecord.class))).thenAnswer(invocation -> {
      EmploymentRecord savedEmploymentRecord = invocation.getArgument(0);
      savedEmploymentRecord.setId(1L);
      return savedEmploymentRecord;
    });

    EmploymentRecordDto result = employmentRecordService
        .createEmploymentRecord(employmentRecordDto, 8883L);

    assertNotNull(result);
    assertEquals(employmentRecordDto, result);
  }

  @Test
  public void updateTest() {
    when(employmentRecordMapper.toDto(any(EmploymentRecord.class))).thenReturn(employmentRecordDto);
    when(employmentRecordRepository.findById(anyLong())).thenReturn(
        Optional.of(new EmploymentRecord()));
    when(employmentRecordRepository.save(any())).thenReturn(new EmploymentRecord());

    EmploymentRecordDto result = employmentRecordService.update(employmentRecordDto);

    assertNotNull(result);
    assertEquals(employmentRecordDto, result);

  }

  @Test
  public void updateWithUnExpectedIdTest() {
    when(employmentRecordRepository.findById(anyLong())).thenThrow(
        EmploymentRecordNotFoundException.class);

    assertThrows(EmploymentRecordNotFoundException.class, () -> {
      employmentRecordService.update(employmentRecordDto);
    });
  }


}