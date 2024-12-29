package com.ratifire.devrate.security.model;

public record CognitoUserInfo(
    String firstName,
    String lastName,
    String email,
    String subject,
    String provider,
    String linkedRecord,
    String cognitoUsername
) {}