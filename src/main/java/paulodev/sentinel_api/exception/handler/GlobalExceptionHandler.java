package paulodev.sentinel_api.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import paulodev.sentinel_api.exception.custom.auth.ExpiredJwtTokenException;
import paulodev.sentinel_api.exception.custom.auth.InvalidJwtTokenException;
import paulodev.sentinel_api.exception.custom.auth.UnauthorizedAccessException;
import paulodev.sentinel_api.exception.custom.user.EmailAlreadyInUseException;
import paulodev.sentinel_api.exception.custom.user.InvalidCredentialsException;
import paulodev.sentinel_api.exception.custom.user.UserNotFoundException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /// User Exceptions

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException( UserNotFoundException exception, HttpServletRequest request ) {
        ErrorResponse error = new ErrorResponse(
                404,
                "NOT_FOUND",
                exception.getMessage(),
                request.getRequestURI(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> invalidCredentialsException( InvalidCredentialsException exception, HttpServletRequest request ) {
        ErrorResponse error = new ErrorResponse(
                401,
                "UNAUTHORIZED",
                exception.getMessage(),
                request.getRequestURI(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> emailAlreadyInUseException( EmailAlreadyInUseException exception, HttpServletRequest request ) {
        ErrorResponse error = new ErrorResponse(
                409,
                "CONFLICT",
                exception.getMessage(),
                request.getRequestURI(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /// Auth Exceptions

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> unauthorizedAccessException( UnauthorizedAccessException exception, HttpServletRequest request ) {
        ErrorResponse error = new ErrorResponse(
                403,
                "FORBIDDEN",
                exception.getMessage(),
                request.getRequestURI(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ErrorResponse> invalidJwtTokenException( InvalidJwtTokenException exception, HttpServletRequest request ) {
        ErrorResponse error = new ErrorResponse(
                401,
                "UNAUTHORIZED",
                exception.getMessage(),
                request.getRequestURI(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ExpiredJwtTokenException.class)
    public ResponseEntity<ErrorResponse> expiredJwtTokenException( ExpiredJwtTokenException exception, HttpServletRequest request ) {
        ErrorResponse error = new ErrorResponse(
                401,
                "UNAUTHORIZED",
                exception.getMessage(),
                request.getRequestURI(),
                Instant.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
