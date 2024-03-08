package com.boom.producesyncbe.repository;

import com.boom.producesyncbe.Data.IdCount;
import com.boom.producesyncbe.Data.UserProfile;
import com.boom.producesyncbe.sellerData.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository  extends MongoRepository<Product,String> {
    List<Product> findBySellerId(String sellerId);

    Product findByProductId(String productId);

}
