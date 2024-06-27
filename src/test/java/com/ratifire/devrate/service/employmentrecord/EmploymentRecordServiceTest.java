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
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the EmploymentRecordService class.
 */
@ExtendWith(MockitoExtension.class)
class EmploymentRecordServiceTest {

  @InjectMocks
  private EmploymentRecordService employmentRecordService;
  @Mock
  private EmploymentRecordRepository employmentRecordRepository;
  @Mock
  private DataMapper<EmploymentRecordDto, EmploymentRecord> mapper;
  private EmploymentRecordDto employmentRecordDto;
  private EmploymentRecord employmentRecord;
  private final long employmentId = 1L;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void before() {
    employmentRecordDto = EmploymentRecordDto.builder()
        .id(employmentId)
        .startDate(LocalDate.of(2023, 1, 1))
        .endDate(LocalDate.of(2023, 11, 22))
        .position("Java Developer")
        .companyName("Example Company 4")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("Was collecting dandelions", "Hello World", "3"))
        .build();

    employmentRecord = EmploymentRecord.builder()
        .id(employmentId)
        .startDate(LocalDate.of(2023, 1, 1))
        .endDate(LocalDate.of(2023, 11, 22))
        .position("Java Developer")
        .companyName("Example Company 4")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("Was collecting dandelions", "Hello World", "3"))
        .build();
  }

  @Test
  void updateTest() {
    when(mapper.toDto(any(EmploymentRecord.class))).thenReturn(employmentRecordDto);
    when(employmentRecordRepository.findById(anyLong())).thenReturn(Optional.of(employmentRecord));
    when(employmentRecordRepository.save(any())).thenReturn(new EmploymentRecord());

    EmploymentRecordDto result = employmentRecordService.update(anyLong(), employmentRecordDto);

    assertNotNull(result);
    assertEquals(employmentRecordDto, result);
  }

  @Test
  void updateWithUnExpectedIdTest() {
    when(employmentRecordRepository.findById(anyLong())).thenThrow(
        EmploymentRecordNotFoundException.class);

    assertThrows(EmploymentRecordNotFoundException.class, () -> {
      employmentRecordService.update(anyLong(), employmentRecordDto);
    });
  }
}