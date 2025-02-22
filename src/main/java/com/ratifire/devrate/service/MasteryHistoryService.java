package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.MasteryHistoryDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.MasteryHistory;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.MasteryHistoryRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for handling mastery history operations.
 */
@Service
@RequiredArgsConstructor
public class MasteryHistoryService {

  private final MasteryHistoryRepository masteryHistoryRepository;
  private final DataMapper<MasteryHistoryDto, MasteryHistory> masteryHistoryMapper;

  /**
   * Retrieves the history of a Mastery within a specified date range.
   *
   * @param masteryId the ID of the Mastery to retrieve history for
   * @param from the start date of the date range (inclusive)
   * @param to the end date of the date range (inclusive)
   * @return a list of MasteryHistoryDto containing the history entries for the specified
   *         Mastery ID within the given date range
   */
  public List<MasteryHistoryDto> getMasteryHistory(Long masteryId, LocalDate from, LocalDate to) {
    List<MasteryHistory> histories = masteryHistoryRepository
        .findByMasteryIdAndDateBetween(masteryId, from, to);
    return masteryHistoryMapper.toDto(histories);
  }

  /**
   * Saves the current state of the Mastery entity into the history.
   *
   * @param mastery the Mastery entity whose current state is to be saved in the history.
   */
  public void saveHistory(Mastery mastery) {
    MasteryHistory history = MasteryHistory.builder()
        .mastery(mastery)
        .date(LocalDate.now())
        .hardSkillMark(mastery.getHardSkillMark())
        .softSkillMark(mastery.getSoftSkillMark())
        .build();
    masteryHistoryRepository.save(history);
  }
}