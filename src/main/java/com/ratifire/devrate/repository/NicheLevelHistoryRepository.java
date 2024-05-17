package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.NicheLevelHistory;
import com.ratifire.devrate.entity.NicheLevelHistoryId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for niche level history entities.
 */
@Repository
public interface NicheLevelHistoryRepository extends JpaRepository<NicheLevelHistory, Long> {

  List<NicheLevelHistory> findById_NicheId(Long nicheId);
}
