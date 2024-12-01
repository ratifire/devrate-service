package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Education entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface EducationRepository extends JpaRepository<Education, Long> {

}
