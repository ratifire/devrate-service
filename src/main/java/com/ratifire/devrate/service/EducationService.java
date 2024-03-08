package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationService {

  private final EducationRepository educationRepository;

  public EducationDto get(){
    // TODO
    return null;

  }

  public EducationDto create(EducationDto educationDto){
    // TODO
    return null;
  }

  public EducationDto update( EducationDto educationDto){
    // TODO
    return null;
  }

  public void delete(long id){
    // TODO
  }

}
