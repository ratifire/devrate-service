package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.UserPersonalInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on User Personal Info entities.
 * This interface provides methods for accessing and managing User Personal Info entities in the
 * database.
 */
@Repository
public interface UserPersonalInfoRepository extends JpaRepository<UserPersonalInfo, Long> {

  Optional<UserPersonalInfo> findByUserId(long userId);

  boolean existsByUserId(long userId);

  void deleteByUserId(long userId);
}
