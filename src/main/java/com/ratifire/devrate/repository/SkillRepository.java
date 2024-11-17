package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Skill} entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface SkillRepository extends JpaRepository<Skill, Long> {

}
