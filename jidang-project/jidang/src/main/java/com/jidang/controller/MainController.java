package com.jidang.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String main() {
        return "mainpage"; // src/main/resources/templates/mainpage.html을 렌더링
    }
}
