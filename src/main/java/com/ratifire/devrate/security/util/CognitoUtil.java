package com.ratifire.devrate.security.util;

import com.amazonaws.services.cognitoidp.model.AttributeType;

/**
 * Utility class for AWS Cognito.
 */
public class CognitoUtil {

  private CognitoUtil() {
  }

  /**
   * Creates a Cognito AttributeType with the specified name and value.
   *
   * @param name  the name of the attribute.
   * @param value the value of the attribute.
   * @return an AttributeType object with the given name and value.
   */
  public static AttributeType createAttribute(String name, String value) {
    return new AttributeType()
        .withName(name)
        .withValue(value);
  }

}