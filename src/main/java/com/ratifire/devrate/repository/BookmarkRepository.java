package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Bookmark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Bookmark entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  @Query(value = "SELECT user_id FROM bookmarks WHERE id = :resourceId", nativeQuery = true)
  Optional<Long> findUserIdByBookmarkId(@Param("resourceId") long resourceId);
}
