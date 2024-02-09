package com.boom.producesyncbe.service.impl;

import com.boom.producesyncbe.Data.*;
import com.boom.producesyncbe.commonutils.HelperFunction;
import com.boom.producesyncbe.config.JwtService;
import com.boom.producesyncbe.repository.AddressRepository;
import com.boom.producesyncbe.repository.UserProfileRepository;
import com.boom.producesyncbe.service.AutoIncrementService;
import com.boom.producesyncbe.service.CreateUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CreateUserServiceImpl implements CreateUserService {
    @Autowired
    private UserProfileRepository repository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private AutoIncrementService autoIncrementService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${opencage.key}")
    private String openCageKey;
    @Value("${opencage.url}")
    private String openCageUrl;

    @Override
    public ResponseEntity<AuthenticationResponse> createUser(UserProfile userProfile,Role role) {
        try {
            //Create User
            UserProfile existingUserProfile = repository.findByUsername(userProfile.getUsername());
            if(Objects.nonNull(existingUserProfile)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthenticationResponse());
            }
            //Get the geocode
            OpenCageResponseDTO responseDTO = getGeocode(userProfile.getAddress());
            if(Objects.isNull(responseDTO)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthenticationResponse());
            }
            userProfile.setId(autoIncrementService.getOrUpdateIdCount(role.name()));
            userProfile.setCreatedTs(Instant.now().toEpochMilli());
            userProfile.setPassword(passwordEncoder.encode(userProfile.getPassword()));
            userProfile.setRole(role);
            repository.insert(userProfile);

            //Insert Address
            Address address = userProfile.getAddress();
            address.setId(userProfile.getId());
            //Set location as specified in the mongodb geospecial document
            Location location = new Location();
            List<Double> lnglat = new ArrayList<>();
            lnglat.add(responseDTO.getResults().get(0).getGeometry().getLng());
            lnglat.add(responseDTO.getResults().get(0).getGeometry().getLat());
            location.setCoordinates(lnglat);
            address.setLocation(location);

            address.setRole(userProfile.getRole());
            addressRepository.save(address);

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
    public ResponseEntity<AuthenticationResponse> authenticate(UserProfile userProfile, Role role) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userProfile.getUsername(),
                        userProfile.getPassword()
                ));
        var user = repository.findByUsername(userProfile.getUsername());
        if(!user.getRole().equals(role)){
            return ResponseEntity.status(403).body(new AuthenticationResponse());
        }
        var jwtToken = jwtService.generateToken(user);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
        return ResponseEntity.ok(authenticationResponse);
    }

    public OpenCageResponseDTO getGeocode(Address address) throws JsonProcessingException {
        String encodedAddress = address.getAddressLine1()+", "+ address.getCity()
                                +", "+address.getProvince()+", "+ address.getCountry()
                                +", "+address.getPostalCode();
        encodedAddress = URLEncoder.encode(encodedAddress);
        System.out.println(encodedAddress);
        String apiUrl = String.format("%s?q=%s&key=%s", openCageUrl, encodedAddress, openCageKey);
        System.out.println(apiUrl);

        ResponseEntity<OpenCageResponseDTO> responseEntity = restTemplate.getForEntity(apiUrl, OpenCageResponseDTO.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            // Handle error cases
            return new OpenCageResponseDTO();
        }
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
