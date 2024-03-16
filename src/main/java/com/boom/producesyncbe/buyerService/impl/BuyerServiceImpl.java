package com.boom.producesyncbe.buyerService.impl;

import com.boom.producesyncbe.Data.Address;
import com.boom.producesyncbe.Data.Role;
import com.boom.producesyncbe.Data.UserProfile;
import com.boom.producesyncbe.buyerData.Order;
import com.boom.producesyncbe.buyerData.OrderProduct;
import com.boom.producesyncbe.buyerData.Status;
import com.boom.producesyncbe.buyerService.BuyerService;
import com.boom.producesyncbe.commonutils.HelperFunction;
import com.boom.producesyncbe.repository.*;
import com.boom.producesyncbe.sellerData.Product;
import com.boom.producesyncbe.service.AutoIncrementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class BuyerServiceImpl implements BuyerService {
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    private AutoIncrementService autoIncrementService;

    @Override
    public ResponseEntity<List<Product>> getNearBySellerProducts(String buyerId, String searchTerm) {
        Optional<Address> buyerAddress = addressRepository.findById(buyerId);
        List<Product> products = new ArrayList<>();
        if(buyerAddress.isPresent()){
            Address address = buyerAddress.get();
            //Sellers in the 10 km radius
            List<Address> nearBySellers = addressRepository.findNearbyAddresses(Role.SELLER.toString(),
                    address.getLocation().getCoordinates().get(0),
                    address.getLocation().getCoordinates().get(1),
                    0, 20000);
            List<String> sellerIdList = new ArrayList<>();
            nearBySellers.forEach(seller->{
                sellerIdList.add(seller.getId());
                //products.addAll(productRepository.findBySellerId(seller.getId()));
            });
            Sort sortByPerUnitPrice = Sort.by(Sort.Direction.ASC, "perUnitPrice");
            products = productRepository.findBySellerIdInAndProductNameRegex(sellerIdList, searchTerm, sortByPerUnitPrice);
        }
        return ResponseEntity.ok(products);
    }

    @Override
    public ResponseEntity<Product> getProductDetails(String productId) {
        return ResponseEntity.ok(productRepository.findByProductId(productId));
    }

    @Override
    public ResponseEntity<Order> addProductToCart(OrderProduct orderProduct, String buyerId) {
        Product productDetails = productRepository.findByProductId(orderProduct.getProductId());
        //Product quantity not available
        if (Objects.isNull(orderProduct.getQuantity()) || orderProduct.getQuantity() > productDetails.getAvailableQuantity()) {
            return ResponseEntity.status(422).body(new Order());
        }
        List<Order> openOrderList = orderRepository.findByBuyerIdAndStatus(buyerId, Status.OPEN.toString());
        if (openOrderList.isEmpty()) {
            return createOrder(orderProduct, productDetails, buyerId);
        }
        if (openOrderList.size() == 1) {
            Order openOrder = openOrderList.get(0);
            return addToExistingOpenOrder(openOrder, orderProduct, productDetails, buyerId);
        } else {
            return ResponseEntity.status(404).body(new Order());

        }
    }

    private ResponseEntity<Order> addToExistingOpenOrder(Order openOrder, OrderProduct orderProduct, Product productDetails, String buyerId) {
        orderProduct.setSellerId(productDetails.getSellerId());
        orderProduct.setProductName(productDetails.getProductName());
        orderProduct.setProductDescription(productDetails.getProductDescription());
        orderProduct.setProductImages(productDetails.getProductImages());
        orderProduct.setPerUnitPrice(productDetails.getPerUnitPrice());
        orderProduct.setUnit(productDetails.getUnit());
        orderProduct.setTotal(HelperFunction.roundUp(orderProduct.getQuantity()*orderProduct.getPerUnitPrice()));

        //If the order is of a different seller then change the seller and remove all the products
        //There can be only one seller in the cart
        if(!openOrder.getSellerId().equals(orderProduct.getSellerId())){
            openOrder.setSellerId(productDetails.getSellerId());
            openOrder.setBrandName(productDetails.getBrandName());
            openOrder.setProductList(List.of(orderProduct));
            openOrder.setOrderTotal(orderProduct.getTotal());
        }else{
            openOrder.getProductList().stream()
                    .filter(product -> product.getProductId().equals(orderProduct.getProductId())) // Replace 'targetProductId' with the actual ID you are looking for
                    .forEach(prd->{
                        // Update the quantity of the found product
                        int newQuantity = orderProduct.getQuantity() + prd.getQuantity();
                        orderProduct.setQuantity(newQuantity);
                        orderProduct.setTotal(HelperFunction.roundUp(orderProduct.getQuantity()*orderProduct.getPerUnitPrice()));
                    });
            //Remove if product exists and later add the updated new product
            openOrder.getProductList().removeIf(product -> product.getProductId().equals(orderProduct.getProductId()));

            //Product Quantity not available
            if(orderProduct.getQuantity() > productDetails.getAvailableQuantity()){
                return ResponseEntity.status(422).body(new Order());
            }else {
                openOrder.getProductList().add(orderProduct);
            }
            //Calculate the new total
            AtomicReference<Double> total = new AtomicReference<>(0.0);
            openOrder.getProductList().forEach(prod->{
                total.updateAndGet(v -> v + prod.getTotal());
            });
            openOrder.setOrderTotal(HelperFunction.roundUp(total.get()));
        }
        openOrder.setUpdatedTs(Instant.now().toEpochMilli());
        orderRepository.save(openOrder);
        return ResponseEntity.ok(openOrder);
    }

    private ResponseEntity<Order> createOrder(OrderProduct orderProduct, Product productDetails, String buyerId) {
        orderProduct.setSellerId(productDetails.getSellerId());
        orderProduct.setProductName(productDetails.getProductName());
        orderProduct.setProductDescription(productDetails.getProductDescription());
        orderProduct.setProductImages(productDetails.getProductImages());
        orderProduct.setPerUnitPrice(productDetails.getPerUnitPrice());
        orderProduct.setUnit(productDetails.getUnit());
        orderProduct.setTotal(HelperFunction.roundUp(orderProduct.getQuantity()*orderProduct.getPerUnitPrice()));

        Order order = new Order();
        order.setOrderId(autoIncrementService.getOrUpdateIdCount("ORDER"));
        order.setBuyerId(buyerId);
        order.setSellerId(productDetails.getSellerId());
        order.setBrandName(productDetails.getBrandName());
        order.getProductList().add(orderProduct);
        order.setOrderTotal(orderProduct.getTotal());
        order.setStatus(Status.OPEN);

        orderRepository.save(order);

        return ResponseEntity.ok(order);
    }

    @Override
    public ResponseEntity<Order> fetchCartDetails(String buyerId) {
        List<Order> openOrderList = orderRepository.findByBuyerIdAndStatus(buyerId, Status.OPEN.toString());
        if (openOrderList.isEmpty()) {
            return ResponseEntity.ok().body(new Order());
        }else{
            return ResponseEntity.ok(openOrderList.get(0));
        }
    }

    @Override
    public ResponseEntity<Order> confirmOrder(String buyerId) {
        List<Order> openOrderList = orderRepository.findByBuyerIdAndStatus(buyerId, Status.OPEN.toString());
        if (openOrderList.isEmpty()) {
            return ResponseEntity.badRequest().body(new Order());
        }else{
            Order openOrder = openOrderList.get(0);
            if(openOrder.getProductList().isEmpty()){
                return ResponseEntity.badRequest().body(new Order());
            }
            openOrder.setStatus(Status.CONFIRMED);
            openOrder.setUpdatedTs(Instant.now().toEpochMilli());

            // For Quantity availably
            openOrder.getProductList().forEach(orderProduct->{
                Product pricePrd = productRepository.findByProductId(orderProduct.getProductId());
                if (orderProduct.getQuantity() > pricePrd.getAvailableQuantity()) {
                    openOrder.getProductQtyExceeded().add(pricePrd.getProductName());
                }
            });
            if(!openOrder.getProductQtyExceeded().isEmpty()){
                return ResponseEntity.status(422).body(openOrder);
            }else{
                openOrder.getProductList().forEach(orderProduct->{
                    Product pricePrd = productRepository.findByProductId(orderProduct.getProductId());
                    pricePrd.setAvailableQuantity(pricePrd.getAvailableQuantity()-orderProduct.getQuantity());
                    productRepository.save(pricePrd);
                });
            }
            UserProfile userProfile = userProfileRepository.findByUserId(buyerId);
            openOrder.setBuyerName(userProfile.getFirstName() + " " + userProfile.getLastName());
            Address buyerAddress = addressRepository.findByUserId(buyerId);
            openOrder.setAddress(buyerAddress);
            orderRepository.save(openOrder);
            return ResponseEntity.ok(openOrder);
        }
    }

    @Override
    public ResponseEntity<Order> deleteProduct(String buyerId, String productId) {
        List<Order> openOrderList = orderRepository.findByBuyerIdAndStatus(buyerId, Status.OPEN.toString());
        if (openOrderList.isEmpty()) {
            return ResponseEntity.badRequest().body(new Order());
        }else {
            Order openOrder = openOrderList.get(0);
            //Remove if product exists
            openOrder.getProductList().removeIf(product -> product.getProductId().equals(productId));
            //Calculate the new total
            AtomicReference<Double> total = new AtomicReference<>(0.0);
            openOrder.getProductList().forEach(prod->{
                total.updateAndGet(v -> v + prod.getTotal());
            });
            openOrder.setOrderTotal(HelperFunction.roundUp(total.get()));
            openOrder.setUpdatedTs(Instant.now().toEpochMilli());
            orderRepository.save(openOrder);

            return ResponseEntity.ok(openOrder);
        }
    }

    @Override
    public ResponseEntity<String> isTokenOfUser(String buyerId, String username) {
        UserProfile buyer = userProfileRepository.findByUserId(buyerId);
        if(!buyer.getUsername().isEmpty() && buyer.getUsername().equals(username)){
            return ResponseEntity.ok("The token is of the same user");
        }else{
            return ResponseEntity.status(409).body("The token is not valid for this email");
        }
    }

    @Override
    public Boolean isProductOfSameSeller(OrderProduct orderProduct, String buyerId) {
        Product productDetails = productRepository.findByProductId(orderProduct.getProductId());
        List<Order> openOrderList = orderRepository.findByBuyerIdAndStatus(buyerId, Status.OPEN.toString());
        if (openOrderList.isEmpty()) {
            return true;
        }
        if (openOrderList.size() == 1) {
            Order openOrder = openOrderList.get(0);
            return openOrder.getSellerId().equals(productDetails.getSellerId());
        } else {
            return false;
        }
    }

    @Override
    public ResponseEntity<Address> fetchSellerAddress(String sellerId) {
        return ResponseEntity.ok(addressRepository.findByUserId(sellerId));
    }

    @Override
    public ResponseEntity<UserProfile> fetchUserProfile(String buyerId) {
        UserProfile userProfile = userProfileRepository.findByUserId(buyerId);
        userProfile.setAddress(addressRepository.findByUserId(buyerId));
        return ResponseEntity.ok(userProfile);
    }

}
