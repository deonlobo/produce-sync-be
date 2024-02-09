package com.boom.producesyncbe.buyerService.impl;

import com.boom.producesyncbe.Data.Address;
import com.boom.producesyncbe.Data.Role;
import com.boom.producesyncbe.buyerService.BuyerService;
import com.boom.producesyncbe.repository.AddressRepository;
import com.boom.producesyncbe.repository.ProductRepository;
import com.boom.producesyncbe.sellerData.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BuyerServiceImpl implements BuyerService {
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    ProductRepository productRepository;
    @Override
    public ResponseEntity<List<Product>> getNearBySellerProducts(String buyerId) {
        Optional<Address> buyerAddress = addressRepository.findById(buyerId);
        List<Product> products = new ArrayList<>();
        if(buyerAddress.isPresent()){
            Address address = buyerAddress.get();
            //Sellers in the 10 km radius
            List<Address> nearBySellers = addressRepository.findNearbyAddresses(Role.SELLER.toString(),
                    address.getLocation().getCoordinates().get(0),
                    address.getLocation().getCoordinates().get(1),
                    0, 20000);
            nearBySellers.forEach(seller->{
                products.addAll(productRepository.findBySellerId(seller.getId()));
            });
        }
        return ResponseEntity.ok(products);
    }
}
