package com.utpcodefest.demo.controller;

import com.utpcodefest.demo.dto.CreateProjectRequest;
import com.utpcodefest.demo.dto.ProjectResponses.ProjectCreated;
import com.utpcodefest.demo.dto.ProjectResponses.ProjectList;
import com.utpcodefest.demo.exception.ApiError;
import com.utpcodefest.demo.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Hackathon project CRUD, backed by the MongoDB \"projects\" collection")
public class ProjectController {

	private final ProjectService projects;

	public ProjectController(ProjectService projects) {
		this.projects = projects;
	}

	@Operation(summary = "List projects, newest first")
	@ApiResponse(responseCode = "200", description = "All projects")
	@GetMapping
	public ProjectList list() {
		return new ProjectList(projects.list());
	}

	@Operation(summary = "Submit a project",
			description = "The server sets id and createdAt; any other fields in the body are ignored.")
	@ApiResponse(responseCode = "201", description = "Project saved")
	@ApiResponse(responseCode = "400", description = "Validation failed or the body isn't valid JSON",
			content = @Content(schema = @Schema(implementation = ApiError.class)))
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProjectCreated create(@Valid @RequestBody CreateProjectRequest request) {
		return new ProjectCreated(projects.create(request));
	}
}
