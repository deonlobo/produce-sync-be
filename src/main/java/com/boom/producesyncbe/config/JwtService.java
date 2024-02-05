package com.boom.producesyncbe.config;

import com.boom.producesyncbe.Data.UserProfile;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "cGdiRUJsR3FBVE5QRVBDWWV3dEhseGVBbkt4bWZqaElMalBFaU9VQmpQWUxveGRXbG1zR2ZSb0NTalRXS0RlTldYWXVrd3hwYWRCTm1JQXVlbE9oTW12UHlZRHhFWU90TGZncnhZYUxhSHZQYkhlaUNqVXl5QWNxRnNRTFd0cHM=";
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractClaims(String token){
        return extractAllClaims(token);
    }

    public String generateToken(UserProfile userDetails){
        Map<String, String> extraClaims = new HashMap<>();
        extraClaims.put("role", userDetails.getRole().toString());
        extraClaims.put("id",userDetails.getId());
        return generateToken(extraClaims, userDetails);    }

    public String generateToken(
            Map<String , String> extraClaims,
            UserDetails userDetails
    ){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
