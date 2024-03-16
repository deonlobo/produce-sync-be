package com.boom.producesyncbe.sellerService.impl;

import com.boom.producesyncbe.Data.Address;
import com.boom.producesyncbe.Data.UserProfile;
import com.boom.producesyncbe.buyerData.Order;
import com.boom.producesyncbe.buyerData.Status;
import com.boom.producesyncbe.repository.AddressRepository;
import com.boom.producesyncbe.repository.OrderRepository;
import com.boom.producesyncbe.repository.ProductRepository;
import com.boom.producesyncbe.repository.UserProfileRepository;
import com.boom.producesyncbe.sellerData.Product;
import com.boom.producesyncbe.sellerService.SellerService;
import com.boom.producesyncbe.service.AutoIncrementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    UserProfileRepository userProfileRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    AutoIncrementService autoIncrementService;

    @Override
    public ResponseEntity<Product> addNewProduct(Product product) {
        product.setProductId(autoIncrementService.getOrUpdateIdCount("PRODUCT"));
        product.setTotal(product.getQuantity()*product.getPerUnitPrice());
        product.setAvailableQuantity(product.getQuantity());
        UserProfile seller = userProfileRepository.findByUserId(product.getSellerId());
        product.setBrandName(seller.getBrandName());
        return ResponseEntity.ok(productRepository.save(product));
    }

    @Override
    public ResponseEntity<List<Product>> fetchAllSellerProducts(String sellerId) {
        return ResponseEntity.ok(productRepository.findBySellerId(sellerId));
    }

    @Override
    public ResponseEntity<String> isTokenOfUser(String sellerId, String username) {
        UserProfile buyer = userProfileRepository.findByUserId(sellerId);
        if(!buyer.getUsername().isEmpty() && buyer.getUsername().equals(username)){
            return ResponseEntity.ok("The token is of the same user");
        }else{
            return ResponseEntity.status(404).body("The token is not valid for this email");
        }
    }

    @Override
    public ResponseEntity<Product> getProductDetails(String productId) {
        return ResponseEntity.ok(productRepository.findByProductId(productId));
    }

    @Override
    public ResponseEntity<Product> updateProduct(Product product) {
        Product updatedProd = productRepository.findByProductId(product.getProductId());
        if(Objects.nonNull(product.getProductName())){
            updatedProd.setProductName(product.getProductName());
        }
        if(Objects.nonNull(product.getProductDescription())){
            updatedProd.setProductDescription(product.getProductDescription());
        }
        if(Objects.nonNull(product.getProductImages())){
            updatedProd.setProductImages(product.getProductImages());
        }
        if(Objects.nonNull(product.getQuantity())){
            Integer availableQuantityDif =  product.getQuantity()-updatedProd.getQuantity();
            int availableQty = updatedProd.getAvailableQuantity()+availableQuantityDif;
            if(availableQty<0){
                availableQty=0;
            }
            updatedProd.setQuantity(product.getQuantity());
            updatedProd.setAvailableQuantity(availableQty);
        }
        if(Objects.nonNull(product.getPerUnitPrice())){
            updatedProd.setPerUnitPrice(product.getPerUnitPrice());
        }
        if(Objects.nonNull(product.getUnit())){
            updatedProd.setUnit(product.getUnit());
        }
        updatedProd.setTotal(updatedProd.getQuantity()*updatedProd.getPerUnitPrice());
        productRepository.save(updatedProd);
        return ResponseEntity.ok(updatedProd);
    }

    @Override
    public ResponseEntity<List<Order>> getOrdersStatus(String sellerId, List<String> status) {
        return ResponseEntity.ok(orderRepository.findBySellerIdAndStatusIn(sellerId,status));
    }

    @Override
    public ResponseEntity<Order> setOrderStatus(String sellerId, Order order) {
        Order orderFetched = orderRepository.findByOrderId(order.getOrderId());
        if(Objects.nonNull(orderFetched) && !sellerId.equals(orderFetched.getSellerId())){
            return ResponseEntity.status(409).body(new Order());
        }
        orderFetched.setStatus(order.getStatus());
        orderRepository.save(orderFetched);
        return ResponseEntity.ok(orderFetched);
    }

    @Override
    public ResponseEntity<UserProfile> fetchUserProfile(String buyerId) {
        UserProfile userProfile = userProfileRepository.findByUserId(buyerId);
        userProfile.setAddress(addressRepository.findByUserId(buyerId));
        return ResponseEntity.ok(userProfile);
    }
}
