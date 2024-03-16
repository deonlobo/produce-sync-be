package com.boom.producesyncbe.commonutils;

import com.boom.producesyncbe.config.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ObjectStreamClass;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Service
public class HelperFunction {
    @Autowired
    JwtService jwtService;

    public String getClaimsUserId(String cookieHeader){
        String[] cookies = cookieHeader.split("; ");
        Claims claims = null;
        for (String cookie : cookies) {
            if (cookie.startsWith("authToken=")) {
                 claims = jwtService.extractClaims(cookie.substring("authToken=".length()));
            }
        }
        if(Objects.nonNull(claims))
            return claims.get("id", String.class);
        else
            return null;
    }

    public String getBuyerIdFromToken(String authHeader) {
        // Extract the token from the authorization header
        String token = authHeader.replace("Bearer ", ""); // Remove "Bearer " prefix if present
        Claims claims = jwtService.extractClaims(token);
        if(Objects.nonNull(claims))
            return claims.get("id", String.class);
        else
            return null;
    }

    public static Double roundUp(Double value){
        BigDecimal roundedVal = new BigDecimal(value)
                .setScale(2, RoundingMode.HALF_UP); // Round to 2 decimal places
        return roundedVal.doubleValue();
    }


}
