package com.example.antiquepedia_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/hello")
    public String HelloSam(){
        return "Hello Sam!";
    }

    @RequestMapping("/testJena")
    public String TestJena(){
        // later do jena test
        return "Test OK";
    }
}
