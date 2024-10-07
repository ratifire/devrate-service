package com.ratifire.devrate.repository;

import com.ratifire.devrate.dto.UserNameSearchDto;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.User;
import java.util.List;
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

  List<User> findAllByEventsContaining(Event event);

  @Query("SELECT new com.ratifire.devrate.dto.UserNameSearchDto(u.id, u.firstName, u.lastName) "
      + "FROM User u "
      + "WHERE "
      + "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) "
      + "AND LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) "
      + "OR (LOWER(u.lastName) LIKE LOWER(CONCAT('%', :firstName, '%')) "
      + "AND LOWER(u.firstName) LIKE LOWER(CONCAT('%', :lastName, '%'))) "
      + "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) "
      + "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
  List<UserNameSearchDto> findUsersByName(@Param("firstName") String firstName,
      @Param("lastName") String lastName,
      @Param("query") String query);
}
