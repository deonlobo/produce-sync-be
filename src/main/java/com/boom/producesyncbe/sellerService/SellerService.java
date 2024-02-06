package com.boom.producesyncbe.sellerService;

import com.boom.producesyncbe.sellerData.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SellerService {

     ResponseEntity<Product> addNewProduct(Product product);
     ResponseEntity<List<Product>> fetchAllSellerProducts(String sellerId);
}
