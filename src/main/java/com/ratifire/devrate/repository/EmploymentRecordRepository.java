package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.EmploymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on employment-record entities. This interface
 * provides methods for accessing and managing employment-record entities in the database.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface EmploymentRecordRepository extends JpaRepository<EmploymentRecord, Long> {

}

