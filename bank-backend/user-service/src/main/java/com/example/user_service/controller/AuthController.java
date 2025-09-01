package com.example.user_service.controller;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Register New User
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User existing = userRepo.findByUsername(user.getUsername());
        if (existing != null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Username already exists");
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);
        
        // ✅ Return JSON response
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", "User registered successfully");
        successResponse.put("success", true);
        successResponse.put("userId", savedUser.getUserId());
        successResponse.put("username", savedUser.getUsername());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
   // ✅ Login New User
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        User user = userRepo.findByUsername(loginRequest.getUsername());

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(),user.getUserId());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

}
