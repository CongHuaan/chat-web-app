package com.myweb.chat.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final String DEFAULT_SECRET = "K6t9a1vBz3Qx8H2mN4pR7uYcE5wJ0LkD6sA9fG2hT8mC4vB1nZ7qX3rP5tU9yW";

    private final String secret;
    private final long expirationMs;
    private final Key key;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs
    ) {
        // Fallback về secret mặc định nếu ENV/properties thiếu hoặc quá ngắn (< 32 bytes cho HS256)
        String effectiveSecret = (secret != null && secret.getBytes().length >= 32) ? secret : DEFAULT_SECRET;
        this.secret = effectiveSecret;
        this.expirationMs = expirationMs;
        this.key = Keys.hmacShaKeyFor(effectiveSecret.getBytes());
    }

    public String generateToken(String username, Set<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles.stream().collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        Object rolesObj = parseClaims(token).get("roles");
        if (rolesObj instanceof java.util.List<?> rolesList) {
            return rolesList.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }


    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
