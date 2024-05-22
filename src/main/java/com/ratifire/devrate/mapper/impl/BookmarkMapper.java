package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.BookmarkDto;
import com.ratifire.devrate.entity.Bookmark;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for mapping between Bookmark and BookmarkDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class BookmarkMapper implements DataMapper<BookmarkDto, Bookmark> {

  @Mapping(target = "id", ignore = true)
  public abstract Bookmark toEntity(BookmarkDto bookmarkDto);

  @Mapping(target = "id", ignore = true)
  public abstract Bookmark updateEntity(BookmarkDto bookmarkDto, @MappingTarget Bookmark bookmark);

}