package it.unibo.mongodb;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends MongoRepository<AuthMongoDB, String>{

    Optional<AuthMongoDB> findByUsername(String username);

    @Query("{ 'username' : ?0 }")
    @Update("{ '$set' : { 'token' : ?1 } }")
    void updateToken(String username, String token);
}