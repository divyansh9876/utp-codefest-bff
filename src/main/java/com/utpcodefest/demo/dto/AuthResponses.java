package com.utpcodefest.demo.dto;

import com.utpcodefest.demo.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

public final class AuthResponses {

	private AuthResponses() {
	}

	public record Registered(User user) {
	}

	public record LoggedIn(
			@Schema(description = "Our HMAC-signed JWT; send it as Authorization: Bearer <token>") String token,
			User user) {
	}
}
