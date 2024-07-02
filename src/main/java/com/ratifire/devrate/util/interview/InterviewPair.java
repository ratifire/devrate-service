package com.ratifire.devrate.util.interview;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents a pair of interview participants: a candidate and an interviewer.
 */
@Getter
@Builder
public class InterviewPair<C, I> {

  private C candidate;
  private I interviewer;
}