package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Specialisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Specialisation entities.
 */
@Repository
public interface SpecialisationRepository extends JpaRepository<Specialisation, Long> {

}
