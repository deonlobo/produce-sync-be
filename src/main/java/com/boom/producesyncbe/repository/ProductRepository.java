package com.boom.producesyncbe.repository;

import com.boom.producesyncbe.Data.IdCount;
import com.boom.producesyncbe.sellerData.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository  extends MongoRepository<Product,String> {

}
