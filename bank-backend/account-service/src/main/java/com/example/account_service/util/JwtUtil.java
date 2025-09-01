package com.example.account_service.util;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

    private final String SECRET = "this_is_a_super_secure_key_1234567890!";

    // ✅ Token validate karega
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // ✅ Username extract karega (subject)
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ UserId extract karega (custom claim)
    public Integer extractUserId(String token) {
        return (Integer) getClaims(token).get("userId");
    }

    // ✅ Claims return karne wala helper
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
