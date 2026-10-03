package com.utpcodefest.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Body for creating a local account")
public record RegisterRequest(
		@Schema(example = "Ali Hacker", description = "Display name; defaults to the username")
		@Size(max = 60, message = "Name must be at most 60 characters")
		String name,

		@Schema(example = "ali_hacker", description = "3-30 letters, digits, dots or underscores; case-insensitive")
		@NotBlank(message = "Username is required")
		@Pattern(regexp = "^[A-Za-z0-9._]{3,30}$",
				message = "Username must be 3-30 letters, digits, dots or underscores")
		String username,

		@Schema(example = "ali@utp.edu.my")
		@NotBlank(message = "Email is required")
		@Email(message = "Email must be a valid address")
		@Size(max = 254, message = "Email is too long")
		String email,

		// BCrypt only uses the first 72 bytes, so longer passwords are rejected rather than silently truncated.
		@Schema(example = "CodeFest2026!", minLength = 8, maxLength = 72, format = "password")
		@NotBlank(message = "Password is required")
		@Size(min = 8, max = 72, message = "Password must be 8-72 characters")
		String password) {
}
