package com.ratifire.devrate.mapper;

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

  E updateEntity(D dto, @MappingTarget E entity);
}
