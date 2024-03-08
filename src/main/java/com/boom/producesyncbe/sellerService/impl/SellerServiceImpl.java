package com.boom.producesyncbe.sellerService.impl;

import com.boom.producesyncbe.Data.UserProfile;
import com.boom.producesyncbe.repository.ProductRepository;
import com.boom.producesyncbe.repository.UserProfileRepository;
import com.boom.producesyncbe.sellerData.Product;
import com.boom.producesyncbe.sellerService.SellerService;
import com.boom.producesyncbe.service.AutoIncrementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    UserProfileRepository userProfileRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    AutoIncrementService autoIncrementService;

    @Override
    public ResponseEntity<Product> addNewProduct(Product product) {
        product.setProductId(autoIncrementService.getOrUpdateIdCount("PRODUCT"));
        product.setTotal(product.getQuantity()*product.getPerUnitPrice());
        product.setAvailableQuantity(product.getQuantity());
        Optional<UserProfile> seller = userProfileRepository.findById(product.getSellerId());
        seller.ifPresent(userProfile -> product.setBrandName(userProfile.getBrandName()));
        return ResponseEntity.ok(productRepository.save(product));
    }

    @Override
    public ResponseEntity<List<Product>> fetchAllSellerProducts(String sellerId) {
        return ResponseEntity.ok(productRepository.findBySellerId(sellerId));
    }


}
