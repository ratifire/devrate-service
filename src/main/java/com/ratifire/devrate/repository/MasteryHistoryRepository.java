package com.ratifire.devrate.repository;

import com.ratifire.devrate.dto.MasteryHistoryDto;
import com.ratifire.devrate.entity.MasteryHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for accessing {@link MasteryHistory} data.
 */
public interface MasteryHistoryRepository extends JpaRepository<MasteryHistory, Long> {
  @Query("SELECT new com.ratifire.devrate.dto.MasteryHistoryDto(mh.id, mh.mastery.id, "
      + "mh.timestamp, mh.hardSkillMark, mh.softSkillMark) "
      + "FROM MasteryHistory mh WHERE mh.mastery.id = :masteryId")
  List<MasteryHistoryDto> findHistoriesByMasteryId(@Param("masteryId") Long masteryId);
}