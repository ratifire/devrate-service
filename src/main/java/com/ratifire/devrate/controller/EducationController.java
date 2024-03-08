package com.ratifire.devrate.controller;


import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.entity.Education;
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
@RequestMapping("/user/education")
public class EducationController {

  @GetMapping
  public EducationDto get(){
    // TODO
    return null;

  }

  @PostMapping
  public EducationDto create(@RequestBody EducationDto educationDto){
    // TODO
    return null;
  }

  @PutMapping
  public EducationDto update(@RequestBody EducationDto educationDto){
    // TODO
    return null;
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id){
    // TODO
  }


}
