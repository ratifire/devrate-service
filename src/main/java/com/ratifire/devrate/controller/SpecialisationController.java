package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.SpecialisationDto;
import com.ratifire.devrate.service.SpecialisationService;
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
@RequestMapping("/specialisations")
public class SpecialisationController {

  private final SpecialisationService specialisationService;

  /**
   * Retrieves specialisation by ID.
   *
   * @return the specialisation as a DTO
   */
  @GetMapping("/{id}")
  public SpecialisationDto findById(@PathVariable long id) {
    return specialisationService.findById(id);
  }

  /**
   * Retrieves all specialisations.
   *
   * @return the list of specialisations as a DTO
   */
  @GetMapping()
  public List<SpecialisationDto> getAllSpecialisations() {
    return specialisationService.getAllSpecialisations();
  }

}
