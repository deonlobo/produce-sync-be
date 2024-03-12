package com.boom.producesyncbe.repository;

import com.boom.producesyncbe.Data.Address;
import com.boom.producesyncbe.Data.Role;
import com.boom.producesyncbe.Data.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@EnableMongoRepositories
public interface AddressRepository extends MongoRepository<Address,String>,AddressRepoMongoTemplate {

    @Query("{ 'role' : ?0, 'location' : { $near : { $geometry : { type: 'Point', coordinates: [ ?1, ?2 ] }, $minDistance: ?3, $maxDistance: ?4 } } }")
    List<Address> findNearbyAddresses(String role, double longitude, double latitude, double minDistance, double maxDistance);
}
