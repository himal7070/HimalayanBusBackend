package com.himalayanbus.service.implementation;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationTime}")
    private long jwtExpirationTime;

    public String generateJwtToken(Integer userId, String role) {
        Date expirationDate = new Date(System.currentTimeMillis() + jwtExpirationTime * 1000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userId);
        claims.put("role", role);

        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }


    public Claims validateJwtToken(String jwtToken) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

    }





}
