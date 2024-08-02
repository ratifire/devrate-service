package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.MasteryHistory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing {@link MasteryHistory} data.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface MasteryHistoryRepository extends JpaRepository<MasteryHistory, Long> {
  List<MasteryHistory> findByMasteryIdAndDateBetween(long masteryId, LocalDate from, LocalDate to);
}