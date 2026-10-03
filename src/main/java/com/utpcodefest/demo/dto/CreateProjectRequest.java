package com.utpcodefest.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// The only fields a client may send; id and createdAt are always set by the server.
@Schema(description = "Body for submitting a project")
public record CreateProjectRequest(
		@Schema(example = "CampusEats", maxLength = 80)
		@NotBlank(message = "Title is required")
		@Size(max = 80, message = "Title must be at most 80 characters")
		String title,

		@Schema(example = "Order food from UTP cafes and skip the queue.", maxLength = 500)
		@NotBlank(message = "Description is required")
		@Size(max = 500, message = "Description must be at most 500 characters")
		String description,

		@Schema(example = "Web", allowableValues = {
				"AI / ML", "Web", "Mobile", "FinTech", "Sustainability", "HealthTech", "EdTech", "IoT", "Gaming", "Other"})
		@NotBlank(message = "Category is required")
		@Pattern(regexp = CATEGORIES, message = "Unknown category")
		String category,

		@Schema(example = "Null Pointers", maxLength = 60)
		@Size(max = 60, message = "Team name must be at most 60 characters")
		String teamName,

		@Schema(example = "https://github.com/null-pointers/campuseats", maxLength = 300)
		@Size(max = 300, message = "Repo URL must be at most 300 characters")
		String repoUrl) {

	public static final String CATEGORIES =
			"AI / ML|Web|Mobile|FinTech|Sustainability|HealthTech|EdTech|IoT|Gaming|Other";
}
