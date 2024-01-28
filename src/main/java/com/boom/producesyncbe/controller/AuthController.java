package com.boom.producesyncbe.controller;

import com.boom.producesyncbe.Data.AuthenticationResponse;
import com.boom.producesyncbe.Data.UserProfile;
import com.boom.producesyncbe.service.CreateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private CreateUserService createUserService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/create")
    @CrossOrigin
    public ResponseEntity<AuthenticationResponse> createResourceSeller(@RequestBody UserProfile userProfile) {
        return createUserService.createUser(userProfile);
    }

    @GetMapping("/login")
    @CrossOrigin
    public ResponseEntity<AuthenticationResponse> loginSeller(@RequestBody UserProfile userProfile) {
        return ResponseEntity.ok(createUserService.authenticate(userProfile));
    }
}
