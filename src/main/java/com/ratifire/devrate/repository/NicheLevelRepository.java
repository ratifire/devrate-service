package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.NicheLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on niche level entities.
 */
@Repository
public interface NicheLevelRepository extends JpaRepository<NicheLevel, Long> {

}
