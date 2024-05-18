package com.example.backend.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo-controller")
public class demoController {
    @GetMapping
    public ResponseEntity<String> SayHello() {
        return ResponseEntity.ok("Witaj");
    }
}
