package com.boom.producesyncbe.controller;

import com.boom.producesyncbe.Data.UserProfile;
import com.boom.producesyncbe.buyerData.Order;
import com.boom.producesyncbe.commonutils.HelperFunction;
import com.boom.producesyncbe.sellerData.Product;
import com.boom.producesyncbe.sellerService.FileService;
import com.boom.producesyncbe.sellerService.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/seller")
public class SellerController {
    @Autowired
    HelperFunction helperFunction;
    @Autowired
    SellerService sellerService;
    @Autowired
    FileService fileService;

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

    @PostMapping(value="/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<String>> addFile(@RequestHeader("Cookie") String cookieHeader, @RequestParam("files") MultipartFile[] files){
        String directory = helperFunction.getClaimsUserId(cookieHeader);
        return fileService.uploadFiles(files, directory);
    }

    @GetMapping("/product/fetch")
    public ResponseEntity<List<Product>> fetchAllSellerProcucts(@RequestHeader("Cookie") String cookieHeader){
        String sellerId = helperFunction.getClaimsUserId(cookieHeader);
        return sellerService.fetchAllSellerProducts(sellerId);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> isTokenOfUser(@RequestBody String username,
                                                @RequestHeader("Authorization") String authHeader){

        String sellerId = helperFunction.getBuyerIdFromToken(authHeader);
        return sellerService.isTokenOfUser(sellerId,username);
    }

    @GetMapping("/product/details")
    public ResponseEntity<Product> getProductDetails(@RequestParam("productId") String productId,
                                                     @RequestHeader("Cookie") String cookieHeader){
        return sellerService.getProductDetails(productId);
    }

    @PutMapping("/product/update")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product,
                                                 @RequestHeader("Authorization") String authHeader){
        return sellerService.updateProduct(product);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrdersStatus(@RequestParam String status,
                                                       @RequestHeader("Authorization") String authHeader){
        String sellerId = helperFunction.getBuyerIdFromToken(authHeader);
        List<String> statusList = Arrays.asList(status.split(","));
        return sellerService.getOrdersStatus(sellerId, statusList);
    }

    @PutMapping("/order/status")
    public ResponseEntity<Order> setOrderStatus(@RequestBody Order order,
                                                @RequestHeader("Authorization") String authHeader){
        String sellerId = helperFunction.getBuyerIdFromToken(authHeader);
        return sellerService.setOrderStatus(sellerId, order);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> fetchUserProfile(@RequestHeader("Authorization") String authHeader){
        String buyerId = helperFunction.getBuyerIdFromToken(authHeader);
        return sellerService.fetchUserProfile(buyerId);
    }

}
