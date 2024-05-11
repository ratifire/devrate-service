package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.UserSecurity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link UserSecurity} entities. This
 * interface provides methods for accessing and managing {@link UserSecurity} entities in the DB.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long> {

  boolean existsByEmail(String email);

  Optional<UserSecurity> findByEmail(String email);

  Optional<UserSecurity> findByUserId(long id);
}
