package com.boom.producesyncbe.buyerService;

import com.boom.producesyncbe.sellerData.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BuyerService {

    ResponseEntity<List<Product>> getNearBySellerProducts(String buyerId);
}
