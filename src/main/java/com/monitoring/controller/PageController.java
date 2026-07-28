package com.monitoring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        // Ez utasítja a Spring Bootot, hogy a "templates" mappában keresse az "index.html"-t
        return "index"; 
    }
}