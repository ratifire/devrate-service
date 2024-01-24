package com.ratifire.devrate.mapper;


/**
 * The interface Mapper.
 *
 * @param <D> the type parameter
 * @param <E> the type parameter
 */
public interface Mapper<D, E> {

  E toEntity(D dto);

  D toDto(E entity);

}
