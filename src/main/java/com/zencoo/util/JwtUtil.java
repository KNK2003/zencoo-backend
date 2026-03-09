package com.zencoo.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    private final String jwtSecret = "b8d7f6e5c4a3b2d1e0f9a8b7c6d5e4f3b2a1c0d9e8f7b6a5c4d3e2f1b0a9c8d7e6f5a4b3c2d1e0f9a8b7c6d5e4f3"; 
    private final long jwtExpirationMs = 86400000; // 1 day

    public String generateToken(Long userId, String email) {
        logger.info("Generating JWT token for userId={}, email={}", userId, email);
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
            logger.debug("JWT token is valid");
            return true;
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}