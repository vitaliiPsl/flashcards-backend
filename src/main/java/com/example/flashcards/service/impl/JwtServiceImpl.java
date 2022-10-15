package com.example.flashcards.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.flashcards.model.User;
import com.example.flashcards.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtServiceImpl implements JwtService {
    public static final String CLAIM_ROLES = "roles";

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-time-minutes}")
    private long expirationTimeMinutes;

    @Override
    public String createToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        Instant expiresAt = LocalDateTime.now().plusMinutes(expirationTimeMinutes)
                .atZone(ZoneId.systemDefault()).toInstant();

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim(CLAIM_ROLES, authorities)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    @Override
    public Authentication decodeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedToken = verifier.verify(token);

        String username = decodedToken.getSubject();
        String[] roles = decodedToken.getClaim(CLAIM_ROLES).asArray(String.class);
        List<SimpleGrantedAuthority> grantedAuthorities = mapRolesToGrantedAuthorities(roles);

        return new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
    }

    private static List<SimpleGrantedAuthority> mapRolesToGrantedAuthorities(String[] roles) {
        return Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
