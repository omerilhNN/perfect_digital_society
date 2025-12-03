package com.perfectdigitalsociety.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping("/")
    public String redirectToSwagger() {
        return "forward:/swagger-ui.html";
    }

    @GetMapping("/docs")
    public String redirectToSwaggerFromDocs() {
        return "forward:/swagger-ui.html";
    }
}
