package com.ratifire.devrate.security.model.constants;


public class CognitoConstant {

  public static final String PARAM_USERNAME = "USERNAME";
  public static final String PARAM_REFRESH_TOKEN = "REFRESH_TOKEN";
  public static final String PARAM_PASSWORD = "PASSWORD";
  public static final String PARAM_SECRET_HASH = "SECRET_HASH";
  public static final String PARAM_GRANT_TYPE = "grant_type";
  public static final String PARAM_CODE = "code";
  public static final String PARAM_REDIRECT_URI = "redirect_uri";
  public static final String PARAM_RESPONSE_TYPE = "response_type";
  public static final String PARAM_CLIENT_ID = "client_id";
  public static final String PARAM_SCOPE = "scope";
  public static final String PARAM_IDENTITY_PROVIDER = "identity_provider";
  public static final String PARAM_STATE = "state";
  public static final String PARAM_DOMAIN = "domain";

  public static final String ATTRIBUTE_EMAIL = "email";
  public static final String ATTRIBUTE_USER_ID = "custom:userId";
  public static final String ATTRIBUTE_ROLE = "custom:role";
  public static final String ATTRIBUTE_AUTHENTICATION_ERROR = "authentication_error";

  public static final String BASE_PROTOCOL = "https://";
  public static final String HEADER_CONTENT_TYPE = "Content-Type";
  public static final String HEADER_AUTHORIZATION = "Authorization";
  public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
  public static final String REFRESH_TOKEN_COOKIE_NAME = "Refresh-Token";

  public static final String AUTHORIZATION_BASIC_PREFIX = "Basic ";
  public static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

  public static final String OAUTH_AUTHORIZE_URL =
      BASE_PROTOCOL + "{" + PARAM_DOMAIN + "}/oauth2/authorize";
  public static final String OAUTH_TOKEN_URL =
      BASE_PROTOCOL + "{" + PARAM_DOMAIN + "}/oauth2/token";


  private CognitoConstant() {
  }

}
