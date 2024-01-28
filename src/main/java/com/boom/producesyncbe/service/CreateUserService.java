package com.boom.producesyncbe.service;


import com.boom.producesyncbe.Data.AuthenticationResponse;
import com.boom.producesyncbe.Data.UserProfile;
import org.springframework.http.ResponseEntity;

public interface CreateUserService {
    ResponseEntity<AuthenticationResponse> createUser(UserProfile userProfile);

    AuthenticationResponse authenticate(UserProfile userProfile);
/*
    UserDetails loadUserByUsername(String username);*/
}
