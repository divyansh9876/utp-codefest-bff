package com.utpcodefest.demo.service;

import com.utpcodefest.demo.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private static final Logger log = LogManager.getLogger(JwtService.class);

	private final SecretKey key;
	private final Duration lifetime;

	public JwtService(@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
		if (secret == null || secret.isBlank()) {
			log.warn("JWT_SECRET is not set: using a random key, so tokens stop working after every restart");
			this.key = Jwts.SIG.HS256.key().build();
		} else {
			// Throws WeakKeyException for secrets under 32 bytes: fail at startup, not on first login.
			this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		}
		this.lifetime = Duration.ofMinutes(expirationMinutes);
	}

	public String generate(User user) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(user.getId())
				.claim("username", user.getUsername())
				.claim("email", user.getEmail())
				.claim("name", user.getName())
				.claim("provider", user.getProvider())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(lifetime)))
				.signWith(key)
				.compact();
	}

	/** Verifies signature and expiry; throws JwtException if either is wrong. */
	public Claims parse(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}
}
