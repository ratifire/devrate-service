package com.ratifire.devrate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The PiplineController class is a REST controller that handles the '/alive' endpoint.
 * It provides a GET request handler to check if the API is alive and returns a question string.
 */
@RestController
@RequestMapping("/alive")
public class PiplineController {

  /**
   * Returns a string representing the status of the API.
   *
   * @return a string indicating the status of the API
   */
  @GetMapping
  public String alive() {
    return "what is the time complexity of put method in HashMap?"
            +
            " and If you can reverse the tree in O(1) complexity";
  }
}