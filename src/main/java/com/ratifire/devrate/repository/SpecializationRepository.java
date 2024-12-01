package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Specialization;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Specialization} entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

  Optional<Specialization> findSpecializationByUserIdAndMainTrue(Long userId);

  boolean existsSpecializationByUserIdAndMainTrue(Long userId);

  boolean existsSpecializationByUserIdAndName(Long userId, String name);

}
