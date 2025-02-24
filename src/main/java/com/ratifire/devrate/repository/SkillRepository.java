package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Skill;
import java.util.List;
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

  @Query(value = """
      SELECT * FROM skills\s
      WHERE mastery_id = :masteryId\s
      AND name IN (:names)""", nativeQuery = true)
  List<Skill> findByMasteryIdAndNameIn(@Param("masteryId") long masteryId,
      @Param("names") List<String> names);

}
