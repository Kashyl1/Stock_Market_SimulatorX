package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PortfolioNotFoundException.class)
    public ResponseEntity<Object> handlePortfolioNotFoundException(PortfolioNotFoundException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CurrencyNotFoundException.class)
    public ResponseEntity<Object> handleCurrencyNotFoundException(CurrencyNotFoundException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PriceNotAvailableException.class)
    public ResponseEntity<Object> handlePriceNotAvailableException(PriceNotAvailableException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> handleInsufficientFundsException(InsufficientFundsException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AssetNotOwnedException.class)
    public ResponseEntity<Object> handleAssetNotOwnedException(AssetNotOwnedException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientAssetAmountException.class)
    public ResponseEntity<Object> handleInsufficientAssetAmountException(InsufficientAssetAmountException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PortfolioAlreadyExistsException.class)
    public ResponseEntity<Object> handlePortfolioAlreadyExistsException(PortfolioAlreadyExistsException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Object> handleInvalidPasswordException(InvalidPasswordException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConfirmationTextMismatchException.class)
    public ResponseEntity<Object> handleConfirmationTextMismatchException(ConfirmationTextMismatchException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Object> handleAuthenticationFailedException(AuthenticationFailedException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<Object> handleAccountNotVerifiedException(AccountNotVerifiedException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<Object> handleUserNotAuthenticatedException(UserNotAuthenticatedException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<Object> handleEmailSendingException(EmailSendingException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<Object> handleInvalidVerificationTokenException(InvalidVerificationTokenException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<Object> handleEmailNotFoundException(EmailNotFoundException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    public ResponseEntity<Object> handleAccountAlreadyVerifiedException(AccountAlreadyVerifiedException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResendEmailCooldownException.class)
    public ResponseEntity<Object> handleResendEmailCooldownException(ResendEmailCooldownException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<Object> handleInvalidAmountException(InvalidAmountException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("message", message);
        errorBody.put("status", status.value());

        return new ResponseEntity<>(errorBody, status);
    }

}
