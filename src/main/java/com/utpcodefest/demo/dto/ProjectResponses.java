package com.utpcodefest.demo.dto;

import com.utpcodefest.demo.model.Project;
import java.util.List;

// Wrapped responses ({ "projects": [...] }, { "project": {...} }) are the shapes the frontend reads.
public final class ProjectResponses {

	private ProjectResponses() {
	}

	public record ProjectList(List<Project> projects) {
	}

	public record ProjectCreated(Project project) {
	}
}
