package com.zencoo.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private final String jwtSecret = "b8d7f6e5c4a3b2d1e0f9a8b7c6d5e4f3b2a1c0d9e8f7b6a5c4d3e2f1b0a9c8d7e6f5a4b3c2d1e0f9a8b7c6d5e4f3"; 
    private final long jwtExpirationMs = 86400000; // 1 day

    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Long getUserIdFromJwt(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}