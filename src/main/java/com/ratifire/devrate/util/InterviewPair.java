package com.ratifire.devrate.util;

import com.ratifire.devrate.entity.interview.InterviewRequest;

/**
 * Represents a pair of interview participants: a candidate and an interviewer.
 */
public record InterviewPair(InterviewRequest candidate, InterviewRequest interviewer) {

}