package com.ratifire.devrate.controller;

import com.ratifire.devrate.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing bookmarks.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

  private final BookmarkService bookmarkService;

  /**
   * Deletes a bookmark.
   *
   * @param id The ID of the bookmark to delete.
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    bookmarkService.delete(id);
  }
}