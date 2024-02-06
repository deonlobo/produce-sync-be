package com.boom.producesyncbe.service;


import com.boom.producesyncbe.Data.AuthenticationResponse;
import com.boom.producesyncbe.Data.Role;
import com.boom.producesyncbe.Data.UserProfile;
import org.springframework.http.ResponseEntity;

public interface CreateUserService {
    ResponseEntity<AuthenticationResponse> createUser(UserProfile userProfile, Role role);

    ResponseEntity<AuthenticationResponse> authenticate(UserProfile userProfile, Role role);
/*
    UserDetails loadUserByUsername(String username);*/
}
