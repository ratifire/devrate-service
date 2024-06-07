package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Mastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Mastery} entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface MasteryRepository extends JpaRepository<Mastery, Long> {

  boolean existsByIdAndSkills_Name(Long id, String name);

}
