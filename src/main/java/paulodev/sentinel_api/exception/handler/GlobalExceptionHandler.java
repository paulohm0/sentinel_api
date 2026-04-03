package paulodev.sentinel_api.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import paulodev.sentinel_api.exception.custom.auth.*;
import paulodev.sentinel_api.exception.custom.condominium.CondominiumEmptyListException;
import paulodev.sentinel_api.exception.custom.condominium.CondominiumNotFoundException;
import paulodev.sentinel_api.exception.custom.user.EmailAlreadyInUseException;
import paulodev.sentinel_api.exception.custom.user.UserNotFoundException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /// Validação de Formato

    // Não respeita o @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ErrorResponse error = new ErrorResponse( 400, "Field Validation", "Falha na validação: " + errorMessage, request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Quando no registro de usuário, não for inserido a role /User/Admin
    // Essa exception faz a validação de tipos especificos (ENUM...)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> invalidJsonFormat(HttpMessageNotReadableException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(400, "Invalid Json Format", "Permissão de usuário não fornecida", request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /// Regras de Negócio do Usuário

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDenied(AccessDeniedException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(403, "Access Denied", "Somente ADMINs tem permissão para acessar esse recurso", request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFound(UserNotFoundException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(404, "User not found", exception.getMessage(), request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Email ja cadastrado
    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> emailAlreadyInUse(EmailAlreadyInUseException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(409, "Email already in use", exception.getMessage(), request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /// Regras de Negócio do Condomínio

    @ExceptionHandler(CondominiumNotFoundException.class)
    public ResponseEntity<ErrorResponse> condominiumNotFound(CondominiumNotFoundException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(404, "Condominium not found", exception.getMessage(), request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CondominiumEmptyListException.class)
    public ResponseEntity<ErrorResponse> condominiumEmptyList(CondominiumEmptyListException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(404, "Condominium empty list", exception.getMessage(), request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> userDesabled(DisabledException exception, HttpServletRequest request)  {
        ErrorResponse error = new ErrorResponse(403, "User desabled", "Esta conta foi desativada. Entre em contato com o suporte.", request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /// Autenticação e Segurança

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(401, "Bad Credentials", "E-mail não cadastrado ou senha incorreta.", request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // É usado caso o usuário tente acessar algo que pela sua Role, ele não tem acesso
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> unauthorizedAccess(UnauthorizedAccessException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(403, "Unauthorized access", exception.getMessage(), request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ErrorResponse> invalidJwtToken(InvalidJwtTokenException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(401, "Invalid Jwt Token", exception.getMessage(), request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ExpiredJwtTokenException.class)
    public ResponseEntity<ErrorResponse> expiredJwtToken(ExpiredJwtTokenException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(401, "Expired Jwt Token", exception.getMessage(), request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(NullJwtTokenException.class)
    public ResponseEntity<ErrorResponse> nullJwtToken(NullJwtTokenException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(401, "Null Jwt Token", exception.getMessage(), request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ErrorResponse> tokenGeneration(TokenGenerationException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(500, "Token Generation Error", exception.getMessage(), request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /// Banco de Dados

    // Quebra alguma regra física da tabela (ex: texto grande demais)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(409, "Data Integrity Violation", "Conflito de dados. A operação viola as regras do banco de dados.", request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Banco de dados cai ou perde a conexão
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> databaseAccess(DataAccessException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(500, "Database Access", "Ocorreu uma falha de comunicação com o banco de dados.", request.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}