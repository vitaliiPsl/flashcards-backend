package com.example.flashcards.config.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.flashcards.dto.errors.ApiError;
import com.example.flashcards.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenVerifierFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("Authorization header: {}", authorization);

        if (authorization == null || authorization.isBlank() || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.replace("Bearer ", "");
        try {
            Authentication authentication = jwtService.decodeToken(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            log.error("Jwt token failed verification: {}", token, e);
            writeErrorResponse(response, e);
        }
    }

    private void writeErrorResponse(HttpServletResponse response, JWTVerificationException e) throws IOException {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Invalid JWT token", e);
        String jsonResponse = new ObjectMapper().writeValueAsString(apiError);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}
