package com.boom.producesyncbe.repository;

import com.boom.producesyncbe.Data.IdCount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IdCountRepository extends MongoRepository<IdCount,String> {
}
