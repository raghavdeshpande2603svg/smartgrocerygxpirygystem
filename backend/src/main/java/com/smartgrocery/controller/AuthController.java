package com.smartgrocery.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Registration endpoint ready"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login endpoint ready"
        ));
    }
}
