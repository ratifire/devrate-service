package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.WorkExperience;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Work Experience entities. This interface
 * provides methods for accessing and managing Work Experience entities in the database.
 */
@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {

  List<WorkExperience> findByUserId(long userId);
}
