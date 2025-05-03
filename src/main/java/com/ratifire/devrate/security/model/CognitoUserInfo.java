package com.ratifire.devrate.security.model;

/**
 * Record representing user information from Amazon Cognito.
 */
public record CognitoUserInfo(
    String firstName,
    String lastName,
    String email,
    String subject,
    String provider,
    String linkedRecord,
    String cognitoUsername,
    String isPrimaryRecord,
    String isAccountActivated
) {

}