package com.boom.producesyncbe.buyerService;

import com.boom.producesyncbe.Data.Address;
import com.boom.producesyncbe.buyerData.Order;
import com.boom.producesyncbe.buyerData.OrderProduct;
import com.boom.producesyncbe.sellerData.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BuyerService {

    ResponseEntity<List<Product>> getNearBySellerProducts(String buyerId, String searchTerm);

    ResponseEntity<Product> getProductDetails(String productId);

    ResponseEntity<Order> addProductToCart(OrderProduct product, String buyerId);

    ResponseEntity<Order> fetchCartDetails(String buyerId);

    ResponseEntity<Order> confirmOrder(String buyerId);

    ResponseEntity<Order> deleteProduct(String buyerId, String productId);
}
