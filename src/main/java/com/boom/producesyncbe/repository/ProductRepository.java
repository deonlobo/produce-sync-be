package com.boom.producesyncbe.repository;

import com.boom.producesyncbe.Data.IdCount;
import com.boom.producesyncbe.Data.UserProfile;
import com.boom.producesyncbe.sellerData.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository  extends MongoRepository<Product,String> {
    List<Product> findBySellerId(String sellerId);

    Product findByProductId(String productId);

    List<Product> findBySellerIdIn(List<String> sellerIds);

    @Query("{'sellerId': { $in: ?0 }, 'productName': { $regex: ?1, $options: 'i' }}")
    List<Product> findBySellerIdInAndProductNameRegex(List<String> sellerIds, String productName, Sort sort);

}
