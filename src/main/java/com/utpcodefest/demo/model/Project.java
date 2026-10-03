package com.utpcodefest.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("projects")
@Schema(description = "A hackathon project stored in MongoDB")
public class Project {

	@Id
	@Schema(description = "MongoDB ObjectId", example = "6ac0f95588e87dba324c74b8")
	private String id;

	@Schema(example = "CampusEats")
	private String title;

	@Schema(example = "Order food from UTP cafes and skip the queue.")
	private String description;

	@Schema(example = "Web")
	private String category;

	@Schema(example = "Null Pointers", nullable = true)
	private String teamName;

	@Schema(example = "https://github.com/null-pointers/campuseats", nullable = true)
	private String repoUrl;

	// The feed is always sorted newest first, so index it.
	@Indexed(direction = IndexDirection.DESCENDING)
	@Schema(description = "Set by the server")
	private Instant createdAt;
}
