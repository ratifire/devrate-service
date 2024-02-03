package com.ratifire.devrate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alive")
public class PiplineController { //todo delete


    @GetMapping
    public String alive(){
        return "what is the time complexity of put method in HashMap?";
    }
}
