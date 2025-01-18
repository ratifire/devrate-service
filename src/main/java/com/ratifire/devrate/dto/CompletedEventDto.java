package com.ratifire.devrate.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the calendar completed event.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompletedEventDto {

  private long id;
  private long eventId;

  //TODO: This model should be expanded once the design layout is finalized to include
  // * additional fields as per the requirements
}