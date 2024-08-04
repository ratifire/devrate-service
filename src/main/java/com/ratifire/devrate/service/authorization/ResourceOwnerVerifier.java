package com.ratifire.devrate.service.authorization;

/**
 * Interface for verifying if a resource is owned by a specific user.
 */
public interface ResourceOwnerVerifier {
  boolean verifyOwner(long resourceId, long loggedUserId);

}
