package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Education entities.
 */
@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

}
