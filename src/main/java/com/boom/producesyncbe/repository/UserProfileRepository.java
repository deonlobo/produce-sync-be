package com.boom.producesyncbe.repository;

import com.boom.producesyncbe.Data.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
public interface UserProfileRepository extends MongoRepository<UserProfile,String> {
    UserProfile findByUsername(String username);
}
