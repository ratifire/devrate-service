//package com.ratifire.devrate.security.controller;
//
//import com.ratifire.devrate.dto.UserDto;
//import com.ratifire.devrate.security.model.dto.OauthExchangeCodeRequest;
//import com.ratifire.devrate.security.model.enums.OAuthProvider;
//import com.ratifire.devrate.security.service.AuthenticationOauthService;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//import java.io.IOException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * test sso.
// */
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/auth/oauth")
//public class OauthController {
//
//  private final AuthenticationOauthService authenticationOauthService;
//
//  @GetMapping("/login/linkedIn")
//  public void loginLinkedInProvider(HttpServletResponse response, HttpSession session)
//      throws IOException {
//    String redirectUrl = authenticationOauthService.generateOauthRedirectUrl(session,
//        OAuthProvider.LINKEDIN.getProvider());
//    response.sendRedirect(redirectUrl);
//  }
//
//  @GetMapping("/login/google")
//  public void loginGoogleProvider(HttpServletResponse response, HttpSession session)
//      throws IOException {
//    String redirectUrl = authenticationOauthService.generateOauthRedirectUrl(session,
//        OAuthProvider.GOOGLE.getProvider());
//    response.sendRedirect(redirectUrl);
//  }
//
//  @PostMapping("/exchange-code")
//  public UserDto exchangeAuthorizationCode(@Valid @RequestBody OauthExchangeCodeRequest request) {
//    return authenticationOauthService.exchangeAuthCodeForTokens(request);
//  }
//}