package com.boom.producesyncbe.repository;

import com.boom.producesyncbe.buyerData.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order,String> {
    List<Order> findByBuyerIdAndStatus(String buyerId, String status);
    List<Order> findBySellerIdAndStatusIn(String sellerId, List<String> status);

    Order findByOrderId(String orderId);
}
