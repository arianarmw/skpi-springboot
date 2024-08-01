package com.ariana.springboot.services.impl;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ariana.springboot.services.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    private static final String SECRET_KEY = "413F4428472B4B6250655368566D5970337336763979244226452948404D6351";

    public String generateToken(UserDetails userDetails) {
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        logger.info("Generated JWT token for user '{}': {}", userDetails.getUsername(), token);
        return token;
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        String token = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        logger.info("Generated refresh JWT token for user '{}': {}", userDetails.getUsername(), token);
        return token;
    }

    public String extractUserName(String token) {
        logger.info("Extracting Username: {}", token);
        if (token == null || token.isEmpty()) {
            logger.error("JWT token is null or empty.");
            return null;
        }

        String username = extractClaims(token, Claims::getSubject);
        logger.info("Extracted username '{}' from JWT token: {}", username, token);
        return username;
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        logger.info("Extracting claims with token: {}", token);
        if (token == null || token.isEmpty()) {
            logger.error("JWT token is null or empty.");
            throw new IllegalArgumentException("JWT token must not be null or empty.");
        }

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSignInKey() {
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }

    private Claims extractAllClaims(String token) {
        try {
            logger.info("Parsing JWT token: {}", token); // Tambahkan log ini untuk mencetak token
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Exception while parsing JWT token: {}", token, e);
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        logger.info("Validating JWT token for user '{}' (token: {})", userDetails.getUsername(), token);
        final String username = extractUserName(token);
        boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        logger.info("JWT token validation for user '{}': {} (token: {})", userDetails.getUsername(), isValid, token);
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public String generateToken(String username) {
        throw new UnsupportedOperationException("Unimplemented method 'generateToken'");
    }
}
