package com.ratifire.devrate.dto;

import java.time.ZonedDateTime;
import lombok.Builder;

/**
 * Result object returned after creating an interview pair.
 */
@Builder
public record InterviewCreatedResultDto(
    InterviewRequestMailDto interviewerMailDto,
    InterviewRequestMailDto candidateMailDto,
    ZonedDateTime date,
    long candidateInterviewId,
    long interviewerInterviewId
) {}
