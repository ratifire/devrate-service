package com.ratifire.devrate.repository;

import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.entity.Specialization;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Specialization} entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

  Optional<Specialization> findSpecializationByUserIdAndMainTrue(Long userId);

  List<SpecializationDto> getSpecializationsByUserId(Long userId);

  @Query("SELECT s.user.id FROM Specialization s WHERE s.id = :id")
  long getUserIdBySpecializationId(@Param("id") long id);
}
