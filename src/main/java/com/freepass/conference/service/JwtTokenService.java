package com.freepass.conference.service;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.freepass.conference.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Long expirationTime;

    public String buildToken(User user) {
        return Jwts.builder()
            .id(user.getId().toString())
            .subject(user.getUsername())
            .claim("email", user.getEmail())
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusMillis(expirationTime)))
            .signWith(getSecretKey())
            .compact();
    }

    public Claims parseToken(String token) {
        Object parsed = Jwts.parser()
            .verifyWith(getSecretKey())
            .build()
            .parse(token)
            .getPayload();
        if (parsed instanceof Claims claims) {
            return claims;
        }
        else throw new IllegalArgumentException("Invalid token");
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
