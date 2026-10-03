package com.utpcodefest.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// Turns exceptions into the { "error": "..." } shape the frontend shows to the user.
@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> invalid(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(f -> f.getField() + ": " + f.getDefaultMessage())
				.orElse("Invalid request");
		return ResponseEntity.badRequest().body(new ApiError(message));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> unreadable() {
		return ResponseEntity.badRequest().body(new ApiError("Request body must be valid JSON"));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiError> status(ResponseStatusException e) {
		return ResponseEntity.status(e.getStatusCode()).body(new ApiError(String.valueOf(e.getReason())));
	}
}
