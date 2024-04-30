package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.service.AchievementService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/achievements")
public class AchievementController {

  private final AchievementService achievementService;

  @GetMapping("/all/{userId}")
  public List<AchievementDto> getAllByUserId(@PathVariable long userId) {
    return achievementService.getAllByUserId(userId);
  }

  @GetMapping("/{id}")
  public AchievementDto getById(@PathVariable long id) {
    return achievementService.getById(id);
  }

  @PostMapping("/{userId}")
  public AchievementDto create(@PathVariable long userId,
      @RequestBody @Valid AchievementDto achievementDto) {
    return achievementService.create(userId, achievementDto);
  }

  @PutMapping("/{id}")
  public AchievementDto update(@PathVariable long id,
      @RequestBody @Valid AchievementDto achievementDto) {
    return achievementService.update(id, achievementDto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    achievementService.delete(id);
  }
}
