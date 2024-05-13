package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Role entities. This interface provides
 * methods for accessing and managing Role entities in the database.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface RoleRepository extends JpaRepository<Role, Long> {

  /**
   * Retrieves a role entity by its name.
   *
   * @param name The name of the role to retrieve.
   * @return The optional of the role entity with the specified name.
   */
  Optional<Role> findByName(String name);
}
