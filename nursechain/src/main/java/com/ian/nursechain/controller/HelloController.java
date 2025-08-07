package com.ian.nursechain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello"; // This will resolve to a view named "hello"
    }

    @GetMapping("/nursecertifications")
    public String nurse_certification() {
        return "nurseCertifications";
    }

    @GetMapping("/nurse-info")
    public String nurse_info() {
        return "nurseInfo";
    }

    @GetMapping("/hospital")
    public String hospital() {
        return "hospital";
    }
}
