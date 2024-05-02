package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on skill entities. This interface provides
 * methods for accessing and managing skill entities in the database.
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

}
