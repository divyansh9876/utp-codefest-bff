package com.utpcodefest.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Log in with email or username (send one of them) plus the password")
public record LoginRequest(
		@Schema(example = "ali@utp.edu.my", description = "Email; a value without '@' is treated as a username")
		String email,

		@Schema(example = "ali_hacker")
		String username,

		@Schema(example = "CodeFest2026!", format = "password")
		@NotBlank(message = "Password is required")
		String password) {

	// One login field is enough: "ali@utp.edu.my" is an email, "ali_hacker" is a username.
	public String identifier() {
		if (email != null && !email.isBlank()) {
			return email.trim().toLowerCase();
		}
		return username == null ? "" : username.trim().toLowerCase();
	}
}
