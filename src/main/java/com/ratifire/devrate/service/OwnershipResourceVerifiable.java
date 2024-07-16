package com.ratifire.devrate.service;

/**
 * This interface defines the contract for checking resource ownership.
 */
public interface OwnershipResourceVerifiable {

  boolean checkOwnership(long resourceId, long ownerId);

}