package com.utpcodefest.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Is the BFF up, and can it reach MongoDB?")
public class HealthController {

	private final MongoTemplate mongo;

	public HealthController(MongoTemplate mongo) {
		this.mongo = mongo;
	}

	// The frontend's "Ping Database" button calls this through its /api proxy.
	@Operation(summary = "Ping MongoDB", description = "Runs MongoDB's ping command and reports the round-trip latency.")
	@ApiResponse(responseCode = "200", description = "BFF and database are reachable", content = @Content(
			mediaType = "application/json",
			examples = @ExampleObject(value = "{\"status\":\"ok\",\"db\":\"connected\",\"dbName\":\"codefest\",\"latencyMs\":12}")))
	@ApiResponse(responseCode = "503", description = "BFF is up but MongoDB is unreachable", content = @Content(
			mediaType = "application/json",
			examples = @ExampleObject(value = "{\"status\":\"error\",\"db\":\"disconnected\",\"error\":\"Timed out after 30000 ms\"}")))
	@GetMapping("/health")
	public ResponseEntity<Map<String, Object>> health() {
		long started = System.currentTimeMillis();
		try {
			mongo.getDb().runCommand(new Document("ping", 1));
			return ResponseEntity.ok(Map.of(
					"status", "ok",
					"db", "connected",
					"dbName", mongo.getDb().getName(),
					"latencyMs", System.currentTimeMillis() - started));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
					"status", "error",
					"db", "disconnected",
					"error", String.valueOf(e.getMessage())));
		}
	}
}
