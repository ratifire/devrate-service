package com.ratifire.devrate.dto.projection;

/**
 * Projection interface for retrieving firstname and lastname of an user.
 */
public interface UserNameProjection {

  String getFirstName();

  String getLastName();

}