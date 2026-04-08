package sk.posam.fsa.statstracker.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;

import java.util.List;

@Component
class RestSecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    RestSecurityExceptionHandler(HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /** Called when authentication is missing or invalid (401). */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) {
        handlerExceptionResolver.resolveException(
                request, response, null,
                new StatsTrackerException(StatsTrackerException.Type.UNAUTHORIZED,
                        "Authentication required – missing or invalid bearer token"));
    }

    /** Called when the user is authenticated but lacks required role (403). */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) {
        handlerExceptionResolver.resolveException(
                request, response, null,
                new StatsTrackerException(StatsTrackerException.Type.FORBIDDEN,
                        "Access denied – insufficient permissions"));
    }
}
