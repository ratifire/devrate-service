package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.EmploymentRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Work Experience entities. This interface
 * provides methods for accessing and managing Work Experience entities in the database.
 */
@Repository
public interface EmploymentRecordRepository extends JpaRepository<EmploymentRecord, Long> {

  List<EmploymentRecord> findByUserId(long userId);
}
