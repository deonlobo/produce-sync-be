package com.boom.producesyncbe.controller;

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
        System.out.println("Here");
        String directory = helperFunction.getClaimsUserId(cookieHeader);
        return fileService.uploadFiles(files, directory);
    }

    @GetMapping("/product/fetch")
    public ResponseEntity<List<Product>> fetchAllSellerProcucts(@RequestHeader("Cookie") String cookieHeader){
        String sellerId = helperFunction.getClaimsUserId(cookieHeader);
        return sellerService.fetchAllSellerProducts(sellerId);
    }
}
