package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Skill;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Skill} entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface SkillRepository extends JpaRepository<Skill, Long> {

  @Query(value = "SELECT mastery_id FROM skills WHERE id = :resourceId", nativeQuery = true)
  Optional<Long> findMasteryIdBySkillId(@Param("resourceId") long resourceId);

}
