package com.boom.producesyncbe.controller;

import com.boom.producesyncbe.buyerService.BuyerService;
import com.boom.producesyncbe.commonutils.HelperFunction;
import com.boom.producesyncbe.sellerData.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/buyer")
public class BuyerController {
    @Autowired
    HelperFunction helperFunction;
    @Autowired
    BuyerService buyerService;
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello(@RequestHeader("Cookie") String cookieHeader){
        System.out.println(helperFunction.getClaimsUserId(cookieHeader));
        return ResponseEntity.ok("Hello from secured buyer endpoint");
    }

    @GetMapping("/nearby/seller/products")
    public ResponseEntity<List<Product>> getNearBySellerProducts(@RequestHeader("Cookie") String cookieHeader){
        String buyerId = helperFunction.getClaimsUserId(cookieHeader);
        return buyerService.getNearBySellerProducts(buyerId);
    }
}
