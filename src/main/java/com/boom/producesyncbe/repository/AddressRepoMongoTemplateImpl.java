package com.boom.producesyncbe.repository;

import com.boom.producesyncbe.Data.Address;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AddressRepoMongoTemplateImpl implements AddressRepoMongoTemplate {

    private final MongoTemplate mongoTemplate;

    public AddressRepoMongoTemplateImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Address> findSellersNearLocationWithProduct(double longitude, double latitude, double minDistance, double maxDistance, String productNameRegex) {
        LookupOperation lookup = LookupOperation.newLookup()
                .from("product")
                .localField("sellerId")
                .foreignField("sellerId")
                .as("products");

        MatchOperation matchGeoLocation = Aggregation.match(
                new Criteria("location").nearSphere(new GeoJsonPoint(longitude, latitude))
                        .minDistance(minDistance)
                        .maxDistance(maxDistance)
        );

        MatchOperation matchRole = Aggregation.match(
                new Criteria("role").is("SELLER")
        );

        MatchOperation matchProductName = Aggregation.match(
                new Criteria("products.productName").regex(productNameRegex, "i")
        );

        Aggregation aggregation = Aggregation.newAggregation(
                matchGeoLocation,
                matchRole,
                matchProductName
        );

        return mongoTemplate.aggregate(aggregation, "address", Address.class).getMappedResults();
    }
}
