package com.boom.producesyncbe.repository;

import com.boom.producesyncbe.buyerData.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order,String> {
    List<Order> findByBuyerIdAndStatus(String buyerId, String status);
}
