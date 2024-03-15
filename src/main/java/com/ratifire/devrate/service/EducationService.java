package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.exception.EducationNotFoundException;
import com.ratifire.devrate.mapper.EducationMapper;
import com.ratifire.devrate.repository.EducationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationService {

  private final EducationRepository educationRepository;

  private final EducationMapper educationMapper;

  public List<EducationDto> getAll() {
    return educationRepository.findAll().stream().map(educationMapper::toDto).toList();
  }

  public EducationDto getById(long id) {
    return educationRepository.findById(id).map(educationMapper::toDto)
        .orElseThrow(() -> new EducationNotFoundException("Education not found with id: " + id));
  }

  @Transactional
  public EducationDto create(long userId, EducationDto educationDto) {
    Education education = educationMapper.toEntity(educationDto);
    education.setId(userId);
    return educationMapper.toDto(educationRepository.save(education));
  }

  @Transactional
  public EducationDto update(long id, EducationDto educationDto) {
    Education education = educationRepository.findById(id)
        .orElseThrow(() -> new EducationNotFoundException(
            "Education not found with id: " + id));
    educationMapper.toUpdate(educationDto, education);
    return educationMapper.toDto(educationRepository.save(education));
  }

  @Transactional
  public void delete(long id) {
    educationRepository.deleteById(id);
  }
}
