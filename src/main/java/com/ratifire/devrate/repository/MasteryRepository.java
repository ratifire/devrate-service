package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Mastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Mastery} entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface MasteryRepository extends JpaRepository<Mastery, Long> {

  boolean existsByIdAndSkills_Name(Long id, String name);

  @Query(value = "SELECT m.* FROM masteries m JOIN skills s ON m.id = s.mastery_id "
      + "WHERE s.id = :skillId", nativeQuery = true)
  Mastery findMasteryBySkillId(@Param("skillId") long skillId);

}
