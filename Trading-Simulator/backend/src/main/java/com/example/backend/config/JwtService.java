package com.example.backend.config;

import com.example.backend.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Tag(name = "JWT Service", description = "Service for JWT token operations")
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Tag(name = "Extract Username", description = "Extracts username from JWT token")
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Tag(name = "Extract Claim", description = "Extracts specific claim from JWT token")
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Tag(name = "Generate Token", description = "Generates JWT token for user")
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Tag(name = "Generate Token with Claims", description = "Generates JWT token with extra claims")
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        User user = (User) userDetails;
        String role = "ROLE_" + user.getRole().name();
        extraClaims.put("role", role);
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Tag(name = "Is Token Valid", description = "Validates JWT token")
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    @Tag(name = "Is Token Expired", description = "Checks if JWT token is expired")
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Tag(name = "Extract Expiration", description = "Extracts expiration date from JWT token")
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Tag(name = "Extract All Claims", description = "Extracts all claims from JWT token")
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Tag(name = "Get Signing Key", description = "Retrieves the signing key for JWT")
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
