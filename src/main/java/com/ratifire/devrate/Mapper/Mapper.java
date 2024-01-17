package com.ratifire.devrate.Mapper;


public interface Mapper<D, E> {

    E toEntity(D dto);

    D toDto(E entity);

}
