package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Specialization} entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

  @Query("SELECT s FROM Specialization s WHERE s.user.id = :userId AND s.main")
  Specialization findSpecializationByUserIdAndMain(Long userId);
}
