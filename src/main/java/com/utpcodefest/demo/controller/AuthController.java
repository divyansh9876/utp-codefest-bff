package com.utpcodefest.demo.controller;

import com.utpcodefest.demo.dto.AuthResponses.LoggedIn;
import com.utpcodefest.demo.dto.AuthResponses.Registered;
import com.utpcodefest.demo.dto.LoginRequest;
import com.utpcodefest.demo.dto.RegisterRequest;
import com.utpcodefest.demo.exception.ApiError;
import com.utpcodefest.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Local accounts: BCrypt-hashed passwords, login by email or username, JWT on success")
public class AuthController {

	private final AuthService auth;

	public AuthController(AuthService auth) {
		this.auth = auth;
	}

	@Operation(summary = "Create a local account",
			description = "Stores a BCrypt hash, never the password. No token is issued; log in next.")
	@ApiResponse(responseCode = "201", description = "Account created")
	@ApiResponse(responseCode = "400", description = "Validation failed",
			content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "409", description = "Username or email already registered",
			content = @Content(schema = @Schema(implementation = ApiError.class)))
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public Registered register(@Valid @RequestBody RegisterRequest request) {
		return new Registered(auth.register(request));
	}

	@Operation(summary = "Log in with email or username",
			description = "Send either email or username with the password. Returns our JWT.")
	@ApiResponse(responseCode = "200", description = "Credentials valid")
	@ApiResponse(responseCode = "400", description = "Missing email/username or password",
			content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "401", description = "Same message for unknown account and wrong password",
			content = @Content(schema = @Schema(implementation = ApiError.class)))
	@PostMapping("/login")
	public LoggedIn login(@Valid @RequestBody LoginRequest request) {
		return auth.login(request);
	}
}
