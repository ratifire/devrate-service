package com.ratifire.devrate.mapper;

import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.User;
import java.util.List;
import org.mapstruct.MappingTarget;

/**
 * The interface Mapper.
 *
 * @param <D> the type parameter
 * @param <E> the type parameter
 */
public interface DataMapper<D, E> {

  E toEntity(D dto);

  List<E> toEntity(List<D> dto);

  D toDto(E entity);

  List<D> toDto(List<E> entity);

  default D toDto(E entity, User user, List<User> users) {
    throw new UnsupportedOperationException("This method is not supported.");
  }

  default D toDto(E entity, List<Skill> skills) {
    throw new UnsupportedOperationException("This method is not supported.");
  }

  E updateEntity(D dto, @MappingTarget E entity);
}
