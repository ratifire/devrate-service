package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.SpecialisationNameDto;
import com.ratifire.devrate.service.SpecialisationNameService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for getting Specialisations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/specialisations-name")
public class SpecialisationNameController {

  private final SpecialisationNameService specialisationNameService;

  /**
   * Retrieves specialisation by ID.
   *
   * @return the specialisation as a DTO
   */
  @GetMapping("/{id}")
  public SpecialisationNameDto findById(@PathVariable long id) {
    return specialisationNameService.findById(id);
  }

  /**
   * Retrieves all specialisations.
   *
   * @return the list of specialisations as a DTO
   */
  @GetMapping()
  public List<SpecialisationNameDto> getAllSpecialisations() {
    return specialisationNameService.getAllSpecialisations();
  }

}
