package com.ratifire.devrate.service;

import com.ratifire.devrate.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing bookmarks.
 */
@Service
@RequiredArgsConstructor
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;

  /**
   * Deletes a bookmark.
   *
   * @param id The ID of the bookmark to delete.
   */
  public void delete(long id) {
    bookmarkRepository.deleteById(id);
  }
}