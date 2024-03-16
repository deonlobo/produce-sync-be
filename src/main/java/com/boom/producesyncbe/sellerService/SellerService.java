package com.boom.producesyncbe.sellerService;

import com.boom.producesyncbe.Data.UserProfile;
import com.boom.producesyncbe.buyerData.Order;
import com.boom.producesyncbe.sellerData.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SellerService {

     ResponseEntity<Product> addNewProduct(Product product);
     ResponseEntity<List<Product>> fetchAllSellerProducts(String sellerId);

     ResponseEntity<String> isTokenOfUser(String sellerId, String username);

     ResponseEntity<Product> getProductDetails(String productId);

     ResponseEntity<Product> updateProduct(Product product);

     ResponseEntity<List<Order>> getOrdersStatus(String sellerId, List<String> status);

    ResponseEntity<Order> setOrderStatus(String sellerId, Order order);

     ResponseEntity<UserProfile> fetchUserProfile(String buyerId);
}
