package com.ratifire.devrate.dto.projection;

/**
 * Projection interface for retrieving user and mastery information related to an interview event.
 */
public interface InterviewUserMasteryProjection {

  Long getUserId();

  Long getMasteryId();
}