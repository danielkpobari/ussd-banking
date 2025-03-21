package com.daniel.skaet_ussd.security;

import com.daniel.skaet_ussd.dto.ErrorResponse;
import com.daniel.skaet_ussd.exception.AuthenticationException;
import com.daniel.skaet_ussd.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class UssdAuthenticationFilter extends OncePerRequestFilter {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;



public UssdAuthenticationFilter(AuthService authService, AuthenticationManager authenticationManager){
    this.authService = authService;
    this.authenticationManager = authenticationManager;

}


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String sessionId = request.getHeader("X-Session-ID");
            if (sessionId == null || sessionId.isEmpty()) {
                throw new AuthenticationException("Session ID is required");
            }

            authService.validateSession(sessionId);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    new ObjectMapper().writeValueAsString(
                            new ErrorResponse(e.getCode(), e.getMessage(), e.getStatus(), LocalDateTime.now())
                    )
            );
        }
    }
}