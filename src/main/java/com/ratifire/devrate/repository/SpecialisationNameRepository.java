package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.SpecialisationName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Specialisation entities. This interface
 * provides methods for accessing and managing Specialisation entities in the database.
 */
@Repository
public interface SpecialisationNameRepository extends JpaRepository<SpecialisationName, Long> {

}
