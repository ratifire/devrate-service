package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.NicheDto;
import com.ratifire.devrate.dto.NicheLevelDto;
import com.ratifire.devrate.entity.NicheLevel;
import com.ratifire.devrate.service.NicheService;
import jakarta.validation.Valid;
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

/**
 * Controller class for CRUD operations with niche.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/niches")
public class NicheController {

  private final NicheService nicheService;

  /**
   * Retrieves niche by ID.
   *
   * @return the niche as a DTO
   */
  @GetMapping("/{id}")
  public NicheDto findById(@PathVariable long id) {
    return nicheService.findById(id);
  }

  /**
   * Updates niche by niche`s ID.
   *
   * @param nicheDto the updated niche information as a DTO
   * @return the updated niche information as a DTO
   */
  @PutMapping("/{id}")
  public NicheDto update(@RequestBody NicheDto nicheDto, @PathVariable long id) {
    return nicheService.update(nicheDto, id);
  }

  /**
   * Updates niche level history by niche`s ID.
   *
   * @return the updated niche information as a DTO
   */
  @PutMapping("/{nicheId}/history/{levelId}")
  public NicheLevelDto update(@PathVariable long nicheId, @PathVariable long levelId) {
    return nicheService.updateNicheLevelIdAtNiche(nicheId, levelId);
  }

  /**
   * Delete niche by ID.
   *
   * @param id the ID of niche
   */
  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable long id) {
    nicheService.deleteById(id);
  }

  /**
   * Retrieves niche level by niche ID.
   *
   * @return the niche level as a DTO
   */
  @GetMapping("/{nicheId}/niche-level")
  public NicheLevelDto getNicheLevelByNicheId(@PathVariable long nicheId) {
    return nicheService.getNicheLevelByNicheId(nicheId);
  }

  /**
   * Create niche level by niche ID.
   *
   * @param nicheId the ID of niche
   */
  @PostMapping("/{nicheId}/niche-level")
  public NicheLevel createNicheLevel(@Valid @RequestBody NicheLevelDto nicheLevelDto,
      @PathVariable long nicheId) {
    return nicheService.createLevel(nicheLevelDto, nicheId);
  }

  /**
   * Retrieves list of niche levels by niche ID.
   *
   * @return the niche levels as a List
   */
  @GetMapping("/{nicheId}/history")
  public List<NicheLevelDto> getNicheLevelHistory(@PathVariable long nicheId) {
    return nicheService.getNicheLevelHistoryByNicheId(nicheId);
  }

}
