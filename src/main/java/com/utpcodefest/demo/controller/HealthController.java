package com.utpcodefest.demo.controller;

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
public class HealthController {

	private final MongoTemplate mongo;

	public HealthController(MongoTemplate mongo) {
		this.mongo = mongo;
	}

	// The frontend's "Ping Database" button calls this through its /api proxy.
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
