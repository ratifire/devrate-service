package com.ratifire.devrate.service.notification.model;

import com.ratifire.devrate.entity.User;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Data record for interview schedule notification information.
 */
public record InterviewScheduleNotificationData(
    User recipient,
    long interviewId,
    ZonedDateTime interviewDateTime,
    String role,
    String templateName,
    Map<String, Object> emailVariables
) {

}
