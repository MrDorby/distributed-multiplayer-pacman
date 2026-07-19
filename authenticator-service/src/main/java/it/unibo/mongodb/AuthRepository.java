package it.unibo.mongodb;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends MongoRepository<AuthMongoDB, String>{

    Optional<AuthMongoDB> findByUsername(String username);

}