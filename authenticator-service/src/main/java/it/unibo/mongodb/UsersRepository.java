package it.unibo.mongodb;

import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UsersRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersRepository.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UsersRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public UserMongoDB getUserCredentials(String email) {
        try {
            Query query = new Query(Criteria.where("email").is(email));
            List<UserMongoDB> result = mongoTemplate.find(query, UserMongoDB.class);
            return result.getFirst();
        } catch (NoSuchElementException e) {
            LOGGER.error(e.getMessage());
        }
        return null; // TODO: null is good?
    }

    public void addUser(UserMongoDB newUser) {
        if (getUserCredentials(newUser.getUsername()) == null) {
            mongoTemplate.insert(newUser);
        }
    }

}
