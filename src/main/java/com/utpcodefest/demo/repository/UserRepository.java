package com.utpcodefest.demo.repository;

import com.utpcodefest.demo.model.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);
}
