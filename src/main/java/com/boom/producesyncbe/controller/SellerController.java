package com.boom.producesyncbe.controller;

import com.boom.producesyncbe.commonutils.HelperFunction;
import com.boom.producesyncbe.sellerData.Product;
import com.boom.producesyncbe.sellerService.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/seller")
public class SellerController {
    @Autowired
    HelperFunction helperFunction;
    @Autowired
    SellerService sellerService;

    @GetMapping("/hello")
    @CrossOrigin
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello from secured endpoint");
    }

    @PostMapping("/product/create")
    public ResponseEntity<Product> addNewProduct(@RequestHeader("Cookie") String cookieHeader,
                                                 @RequestBody Product product){
        product.setSellerId(helperFunction.getClaimsUserId(cookieHeader));
        return sellerService.addNewProduct(product);
    }
}
