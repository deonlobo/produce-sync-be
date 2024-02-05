package com.boom.producesyncbe.controller;

import com.boom.producesyncbe.commonutils.HelperFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/buyer")
public class BuyerController {
    @Autowired
    HelperFunction helperFunction;
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello(@RequestHeader("Cookie") String cookieHeader){
        System.out.println(helperFunction.getClaimsUserId(cookieHeader));
        return ResponseEntity.ok("Hello from secured buyer endpoint");
    }
}
