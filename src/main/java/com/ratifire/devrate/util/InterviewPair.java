package com.ratifire.devrate.util;

import com.ratifire.devrate.entity.interview.InterviewRequest;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents a pair of interview participants: a candidate and an interviewer.
 */
@Getter
@Builder
public class InterviewPair {

  private InterviewRequest candidate;
  private InterviewRequest interviewer;
}