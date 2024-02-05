package com.boom.producesyncbe.service.impl;

import com.boom.producesyncbe.Data.AuthenticationResponse;
import com.boom.producesyncbe.commonutils.HelperFunction;
import com.boom.producesyncbe.config.JwtService;
import com.boom.producesyncbe.repository.UserProfileRepository;
import com.boom.producesyncbe.Data.Role;
import com.boom.producesyncbe.Data.UserProfile;
import com.boom.producesyncbe.service.AutoIncrementService;
import com.boom.producesyncbe.service.CreateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
public class CreateUserServiceImpl implements CreateUserService {
    @Autowired
    private UserProfileRepository repository;
    @Autowired
    private AutoIncrementService autoIncrementService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Override
    public ResponseEntity<AuthenticationResponse> createUser(UserProfile userProfile,Role role) {
        try {
            UserProfile existingUserProfile = repository.findByUsername(userProfile.getUsername());
            if(Objects.nonNull(existingUserProfile)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthenticationResponse());
            }
            userProfile.setId(autoIncrementService.getOrUpdateIdCount(role.name()));
            userProfile.setCreatedTs(Instant.now().toEpochMilli());
            userProfile.setPassword(passwordEncoder.encode(userProfile.getPassword()));
            userProfile.setRole(role);
            repository.insert(userProfile);
            var jwtToken = jwtService.generateToken(userProfile);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setToken(jwtToken);
            return ResponseEntity.ok(authenticationResponse);
        } catch (Exception e) {
            // Handle the exception, log it, or take any appropriate action.
            e.printStackTrace();
            // Return false if an error occurs during insertion
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setToken("");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthenticationResponse());
        }
    }

    @Override
    public AuthenticationResponse authenticate(UserProfile userProfile) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userProfile.getUsername(),
                        userProfile.getPassword()
                ));
        var user = repository.findByUsername(userProfile.getUsername());
        var jwtToken = jwtService.generateToken(user);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
        return authenticationResponse;
    }

/*    @Override
    public ResponseEntity<?> loginUser(Seller seller) {
        repository.findByUserName(seller.getUserName());

        return ResponseEntity.ok(true);
    }*/

 /*   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Seller user = repository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        // You may customize the UserDetails implementation based on your needs
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }*/


}
