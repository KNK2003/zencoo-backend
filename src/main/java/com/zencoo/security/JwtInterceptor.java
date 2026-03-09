package com.zencoo.security;

import com.zencoo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        logger.info("=== JWT Interceptor === {} {}", method, uri);

        // Allow preflight requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            logger.info("Found Bearer token, length: {}", token.length());

            if (jwtUtil.validateJwtToken(token)) {
                Long userId = jwtUtil.getUserIdFromJwt(token);
                if (userId != null) {
                    request.setAttribute("userId", userId);
                    logger.info("JWT valid, userId={} set as request attribute", userId);
                }
            } else {
                logger.warn("JWT token validation failed");
            }
        } else {
            logger.info("No Bearer token found");
            // Return 401 for protected routes without a token
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return false;
        }

        return true;
    }
}
