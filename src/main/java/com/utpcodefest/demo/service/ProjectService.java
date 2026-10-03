package com.utpcodefest.demo.service;

import com.utpcodefest.demo.dto.CreateProjectRequest;
import com.utpcodefest.demo.model.Project;
import com.utpcodefest.demo.repository.ProjectRepository;
import java.time.Instant;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

	private static final Logger log = LogManager.getLogger(ProjectService.class);

	private final ProjectRepository projects;

	public ProjectService(ProjectRepository projects) {
		this.projects = projects;
	}

	public List<Project> list() {
		return projects.findAllByOrderByCreatedAtDesc();
	}

	public Project create(CreateProjectRequest request) {
		Project project = new Project();
		project.setTitle(request.title().trim());
		project.setDescription(request.description().trim());
		project.setCategory(request.category());
		project.setTeamName(blankToNull(request.teamName()));
		project.setRepoUrl(blankToNull(request.repoUrl()));
		project.setCreatedAt(Instant.now());

		Project saved = projects.save(project);
		log.info("Project created id={} category={}", saved.getId(), saved.getCategory());
		return saved;
	}

	// The form sends "" for untouched optional fields; store those as absent.
	private static String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
