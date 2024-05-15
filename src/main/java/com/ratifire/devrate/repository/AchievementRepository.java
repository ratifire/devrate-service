package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Achievement entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

}
