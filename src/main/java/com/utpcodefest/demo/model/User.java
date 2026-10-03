package com.utpcodefest.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("users")
@Schema(description = "A registered user. Never includes the password hash.")
public class User {

	@Id
	@Schema(example = "6ac0f95588e87dba324c74b9")
	private String id;

	@Schema(example = "Ali Hacker")
	private String name;

	// Stored lowercase; the unique index is the real guard against duplicates.
	@Indexed(unique = true)
	@Schema(example = "ali_hacker")
	private String username;

	@Indexed(unique = true)
	@Schema(example = "ali@utp.edu.my")
	private String email;

	@JsonIgnore
	@Schema(hidden = true)
	private String passwordHash;

	@Schema(example = "local", allowableValues = {"local", "google"})
	private String provider = "local";

	private Instant createdAt;
}
