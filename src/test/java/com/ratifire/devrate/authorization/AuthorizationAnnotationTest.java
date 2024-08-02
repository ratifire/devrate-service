package com.ratifire.devrate.authorization;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

class AuthorizationAnnotationTest {

  private static final String CONTROLLER_PACKAGE_PATH = "com.ratifire.devrate.controller";
  private static final String EXCLUDED_CLASS_NAME = "AuthController";
  private static JavaClasses importedClasses;


  @BeforeAll
  static void setUp() {
    importedClasses = new ClassFileImporter()
        .withImportOption(location -> !location.contains(EXCLUDED_CLASS_NAME))
        .importPackages(CONTROLLER_PACKAGE_PATH);
  }

  @Test
  void resourceControllerMethodsShouldHavePreauthorizeAnnotationTest() {
    methods()
        .that()
        .areDeclaredInClassesThat()
        .areAnnotatedWith(RestController.class)
        .and()
        .areAnnotatedWith(PostMapping.class)
        .or()
        .areAnnotatedWith(PutMapping.class)
        .or()
        .areAnnotatedWith(PatchMapping.class)
        .or()
        .areAnnotatedWith(DeleteMapping.class)
        .should()
        .beAnnotatedWith(PreAuthorize.class)
        .check(importedClasses);
  }
}