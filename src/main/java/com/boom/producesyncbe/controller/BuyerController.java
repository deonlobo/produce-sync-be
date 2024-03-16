package com.boom.producesyncbe.controller;

import com.boom.producesyncbe.Data.Address;
import com.boom.producesyncbe.Data.UserProfile;
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
    public ResponseEntity<List<Product>> getNearBySellerProducts(@RequestParam String search,
                                                                @RequestHeader("Cookie") String cookieHeader){
        String buyerId = helperFunction.getClaimsUserId(cookieHeader);
        return buyerService.getNearBySellerProducts(buyerId, search);
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

    @PostMapping("/cart/product/valid")
    public ResponseEntity<Boolean> isProductOfSameSeller(@RequestBody OrderProduct orderProduct,
                                         @RequestHeader("Authorization") String authHeader){
        String buyerId = helperFunction.getBuyerIdFromToken(authHeader);
        boolean result = buyerService.isProductOfSameSeller(orderProduct, buyerId);
        return ResponseEntity.ok(result);    }

    @GetMapping("/cart/fetch")
    public ResponseEntity<Order> fetchCartDetails(@RequestHeader("Cookie") String cookieHeader){
        String buyerId = helperFunction.getClaimsUserId(cookieHeader);
        return buyerService.fetchCartDetails(buyerId);
    }

    @PutMapping("/cart/confirm")
    public ResponseEntity<Order> confirmOrder(@RequestHeader("Cookie") String cookieHeader){
        String buyerId = helperFunction.getClaimsUserId(cookieHeader);
        return buyerService.confirmOrder(buyerId);
    }

    @PutMapping("/cart/delete/product")
    public ResponseEntity<Order> deleteCart(@RequestParam String productId,
                                            @RequestHeader("Cookie") String cookieHeader){
        String buyerId = helperFunction.getClaimsUserId(cookieHeader);
        return buyerService.deleteProduct(buyerId,productId);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> isTokenOfUser(@RequestBody String username,
                                                @RequestHeader("Authorization") String authHeader){

        String buyerId = helperFunction.getBuyerIdFromToken(authHeader);
        return buyerService.isTokenOfUser(buyerId,username);
    }

    @GetMapping("/product/seller/address")
    public ResponseEntity<Address> fetchSellerAddress(@RequestParam String sellerId){
        return buyerService.fetchSellerAddress(sellerId);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> fetchUserProfile(@RequestHeader("Authorization") String authHeader){
        String buyerId = helperFunction.getBuyerIdFromToken(authHeader);
        return buyerService.fetchUserProfile(buyerId);
    }
}
