package sk.posam.fsa.statstracker.controller;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import sk.posam.fsa.statstracker.rest.dto.ErrorResponseDto;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StatsTrackerException.class)
    public ResponseEntity<ErrorResponseDto> handleDomainException(StatsTrackerException ex, WebRequest request) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class).warn("Domain error: {}", ex.getMessage());
        HttpStatus status = switch (ex.getType()) {
            case VALIDATION -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
        };
        return new ResponseEntity<>(createError(ex.getType().name(), ex.getMessage(), List.of(), request), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            WebRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .toList();
        return new ResponseEntity<>(createError("VALIDATION_ERROR", "Invalid request body", details, request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            WebRequest request) {
        return new ResponseEntity<>(
                createError("VALIDATION_ERROR", "Malformed JSON request body",
                        List.of("Request body is not valid JSON"), request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleHandlerMethodValidation(HandlerMethodValidationException ex,
            WebRequest request) {
        List<String> details = Stream.concat(
                ex.getParameterValidationResults().stream()
                        .flatMap(result -> result.getResolvableErrors().stream()),
                ex.getCrossParameterValidationResults().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList();
        return new ResponseEntity<>(createError("VALIDATION_ERROR", "Invalid request parameters", details, request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(ConstraintViolationException ex,
            WebRequest request) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .toList();
        return new ResponseEntity<>(createError("VALIDATION_ERROR", "Invalid request parameters", details, request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        String detail = ex.getName() + " has invalid value '" + ex.getValue() + "'";
        return new ResponseEntity<>(
                createError("VALIDATION_ERROR", "Invalid request parameters", List.of(detail), request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MissingServletRequestParameterException.class, MissingPathVariableException.class })
    public ResponseEntity<ErrorResponseDto> handleMissingParameter(Exception ex, WebRequest request) {
        return new ResponseEntity<>(createError("VALIDATION_ERROR", "Missing required request parameters",
                List.of(ex.getMessage()), request), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(DataIntegrityViolationException ex,
            WebRequest request) {
        return new ResponseEntity<>(
                createError("CONFLICT", "Request conflicts with existing data",
                        List.of("Duplicate or invalid database state"), request),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponseDto> handleThrowable(Throwable ex, WebRequest request) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Unexpected error", ex);
        return new ResponseEntity<>(
                createError("INTERNAL_ERROR", "Unexpected internal error", List.of(), request),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponseDto createError(String code, String message, List<String> details, WebRequest request) {
        ErrorResponseDto dto = new ErrorResponseDto();
        dto.setCode(code);
        dto.setMessage(message);
        dto.setDetails(details);
        dto.setTimestamp(OffsetDateTime.now());
        dto.setPath(resolvePath(request));
        return dto;
    }

    private String resolvePath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "";
    }
}
