package com.ratifire.devrate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/healthCheck")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<String> aliveMessage() {
        return ResponseEntity.ok("I am alive");
    }
}
