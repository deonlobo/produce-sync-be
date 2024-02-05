package com.boom.producesyncbe.commonutils;

import com.boom.producesyncbe.config.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ObjectStreamClass;
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
}
