package com.ratifire.devrate.service.niche;

import com.ratifire.devrate.entity.NicheLevelHistory;
import com.ratifire.devrate.entity.NicheLevelHistoryId;
import com.ratifire.devrate.repository.NicheLevelHistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing NicheLevelHistory.
 */
@Service
@RequiredArgsConstructor
public class NicheLevelHistoryService {

  private final NicheLevelHistoryRepository historyRepository;

  /**
   * Retrieves NicheLevelHistory by nicheId.
   *
   * @param nicheId the ID of the NicheLevelHistory
   * @return the List NicheLevelHistory as a DTO
   */
  public List<NicheLevelHistory> findByIdNicheId(long nicheId) {
    return historyRepository.findById_NicheId(nicheId);
  }

  /**
   * Saves the niche level history for a given niche and level.
   *
   * @param nicheId The ID of the niche.
   * @param levelId The ID of the level.
   */
  public void saveNicheLevelHistory(long nicheId, long levelId) {
    NicheLevelHistory history = NicheLevelHistory.builder()
        .id(new NicheLevelHistoryId(nicheId, levelId))
        .changeDate(LocalDateTime.now())
        .build();
    historyRepository.save(history);
  }
}
