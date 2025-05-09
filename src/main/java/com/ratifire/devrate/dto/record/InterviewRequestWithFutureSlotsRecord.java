package com.ratifire.devrate.dto.record;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Record representing an interview request with a list of available future time slots.
 *
 * @param id              unique identifier of the interview request
 * @param futureTimeSlots list of future available time slots as ZonedDateTime
 */
public record InterviewRequestWithFutureSlotsRecord(
    long id,
    List<ZonedDateTime> futureTimeSlots) {

}