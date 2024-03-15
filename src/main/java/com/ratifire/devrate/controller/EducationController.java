package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.service.EducationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/educations")
public class EducationController {

  private final EducationService educationService;

  @GetMapping
  public List<EducationDto> getAll() {
    return educationService.getAll();
  }

  @GetMapping("/{id}")
  public EducationDto getById(@PathVariable long id) {
    return educationService.getById(id);
  }

  @PostMapping("/{id}")
  public EducationDto create(@PathVariable int id, @RequestBody EducationDto educationDto) {
    return educationService.create(id, educationDto);
  }

  @PutMapping("/{id}")
  public EducationDto update(@PathVariable int id, @RequestBody EducationDto educationDto) {
    return educationService.update(id, educationDto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    educationService.delete(id);
  }
}
