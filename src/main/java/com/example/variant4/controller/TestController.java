package com.example.variant4.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/")
class TestController {

    @GetMapping("/protected")
    public String protectedRoute(){
        return "protected";
    }

    @GetMapping("/not-protected")
    public String notProtectedRoute(){
        return "not protected drtfyguhjikol";
    }
}
