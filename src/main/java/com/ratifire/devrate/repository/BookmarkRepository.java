package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Bookmark entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

}
