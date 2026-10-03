package com.utpcodefest.demo.service;

import com.utpcodefest.demo.dto.AuthResponses.LoggedIn;
import com.utpcodefest.demo.dto.LoginRequest;
import com.utpcodefest.demo.dto.RegisterRequest;
import com.utpcodefest.demo.model.User;
import com.utpcodefest.demo.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

	private static final Logger log = LogManager.getLogger(AuthService.class);
	private static final String INVALID_CREDENTIALS = "Invalid email/username or password";

	private final UserRepository users;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwt;
	// Compared against when the user doesn't exist, so both failure paths take the same time.
	private final String dummyHash;

	public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwt) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
		this.jwt = jwt;
		this.dummyHash = passwordEncoder.encode("timing-equaliser-not-a-real-password");
	}

	public User register(RegisterRequest request) {
		String username = request.username().trim().toLowerCase();
		String email = request.email().trim().toLowerCase();

		if (users.existsByUsername(username)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
		}
		if (users.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
		}

		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setName(request.name() == null || request.name().isBlank() ? username : request.name().trim());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setProvider("local");
		user.setCreatedAt(Instant.now());

		try {
			User saved = users.save(user);
			log.info("User registered id={} username={}", saved.getId(), saved.getUsername());
			return saved;
		} catch (DuplicateKeyException e) {
			// Two sign-ups raced past the exists checks; the unique index caught it.
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or email is already registered");
		}
	}

	public LoggedIn login(LoginRequest request) {
		String identifier = request.identifier();
		if (identifier.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or username is required");
		}

		Optional<User> found = identifier.contains("@")
				? users.findByEmail(identifier)
				: users.findByUsername(identifier);

		String hash = found.map(User::getPasswordHash).orElse(dummyHash);
		boolean passwordMatches = passwordEncoder.matches(request.password(), hash);

		// Same message whether the account is missing or the password is wrong: no account probing.
		if (found.isEmpty() || found.get().getPasswordHash() == null || !passwordMatches) {
			log.warn("Failed login for identifier={}", identifier);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS);
		}

		User user = found.get();
		log.info("User logged in id={} username={}", user.getId(), user.getUsername());
		return new LoggedIn(jwt.generate(user), user);
	}
}
