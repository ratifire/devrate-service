package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Avatar;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for managing {@link Avatar} entities. This interface extends {@link
 * JpaRepository}, providing standard CRUD operations and custom queries for {@link Avatar} entities
 * based on user ID. It's used to interact with the database layer to perform operations on avatar
 * data.
 */
@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

  /**
   * Retrieves an avatar entity based on the user ID. This method is used to find the avatar
   * associated with a specific user. It returns an {@link Optional} wrapping the {@link Avatar},
   * which will be empty if no avatar is found.
   *
   * @param userId the ID of the user whose avatar is being retrieved.
   * @return an {@link Optional} containing the found {@link Avatar} or an empty {@link Optional} if
   *     no avatar is associated with the provided user ID.
   */
  Optional<Avatar> findByUserId(long userId);

  /**
   * Deletes an avatar entity based on the user ID. This method facilitates the removal of an avatar
   * from the database when it is no longer needed or when the user decides to delete their avatar.
   *
   * @param userId the ID of the user whose avatar is to be deleted.
   */
  void deleteByUserId(long userId);

  /**
   * Checks if an avatar exists for a given user ID. This method is useful for determining whether a
   * user already has an avatar before attempting to add a new one, helping to enforce any business
   * rules around avatar uniqueness per user.
   *
   * @param userId the ID of the user to check for an existing avatar.
   * @return {@code true} if an avatar exists for the specified user ID, {@code false} otherwise.
   */
  boolean existsByUserId(long userId);
}
