package com.boom.producesyncbe.sellerService.impl;

import com.boom.producesyncbe.repository.ProductRepository;
import com.boom.producesyncbe.sellerData.Product;
import com.boom.producesyncbe.sellerService.SellerService;
import com.boom.producesyncbe.service.AutoIncrementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    AutoIncrementService autoIncrementService;

    @Override
    public ResponseEntity<Product> addNewProduct(Product product) {
        product.setProductId(autoIncrementService.getOrUpdateIdCount("PRODUCT"));
        product.setTotal(product.getQuantity()*product.getPerUnitPrice());
        product.setAvailableQuantity(product.getQuantity());
        return ResponseEntity.ok(productRepository.save(product));
    }
}
