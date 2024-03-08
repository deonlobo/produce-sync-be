package com.boom.producesyncbe.controller;

import com.boom.producesyncbe.buyerData.Order;
import com.boom.producesyncbe.buyerData.OrderProduct;
import com.boom.producesyncbe.buyerService.BuyerService;
import com.boom.producesyncbe.commonutils.HelperFunction;
import com.boom.producesyncbe.sellerData.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/product/details")
    public ResponseEntity<Product> getProductDetails(@RequestParam("productId") String productId,
                                                            @RequestHeader("Cookie") String cookieHeader){
        return buyerService.getProductDetails(productId);
    }

    //Create or update the order and add products to the cart order
    @PostMapping("/cart/add")
    public ResponseEntity<Order> addProductToCart(@RequestHeader("Cookie") String cookieHeader,
                                                   @RequestBody OrderProduct orderProduct){
        String buyerId = helperFunction.getClaimsUserId(cookieHeader);
        return buyerService.addProductToCart(orderProduct,buyerId);
    }
}
