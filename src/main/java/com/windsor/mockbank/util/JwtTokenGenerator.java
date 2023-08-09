package com.windsor.mockbank.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;

public class JwtTokenGenerator {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRATION_TIME = 7200000; // Token 過期時間 (2 小時)
    private static final Logger log = LoggerFactory.getLogger(JwtTokenGenerator.class);

    public static String generateJwtToken(String userKey) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        String token = Jwts.builder()
                .setSubject(userKey)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();

        // 添加 "Bearer " 前缀
        return "Bearer " + token;
    }

    public static Jws<Claims> validateJwtToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);

            return claims;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired.");
        } catch (SignatureException e) {
            log.warn("Signature verification failed, JWT token is invalid.");
        } catch (Exception e) {
            log.warn("Exception：{}", e.getMessage());
        }
        return null; // 在出現異常時返回 null
    }
}
