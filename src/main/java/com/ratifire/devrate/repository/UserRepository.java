package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on User entities.
 * This interface provides methods for accessing and managing User Personal Info entities in the
 * database.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u.picture FROM User u WHERE u.id = :id")
  String findPictureByUserId(@Param("id") long id);
}
