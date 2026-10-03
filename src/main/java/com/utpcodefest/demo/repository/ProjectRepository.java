package com.utpcodefest.demo.repository;

import com.utpcodefest.demo.model.Project;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

// Spring Data generates the implementation at startup.
public interface ProjectRepository extends MongoRepository<Project, String> {

	// Query derived from the method name: all projects, newest first.
	List<Project> findAllByOrderByCreatedAtDesc();
}
