package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.MasteryHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository interface for accessing {@link MasteryHistory} data.
 */
public interface MasteryHistoryRepository extends JpaRepository<MasteryHistory, Long> {
  List<MasteryHistory> findHistoriesByMasteryId(Long masteryId);
}

