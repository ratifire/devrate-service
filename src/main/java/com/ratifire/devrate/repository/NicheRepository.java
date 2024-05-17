package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Niche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on niche entities.
 */
@Repository
public interface NicheRepository extends JpaRepository<Niche, Long> {

}
