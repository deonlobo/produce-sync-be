package com.boom.producesyncbe.buyerService.impl;

import com.boom.producesyncbe.Data.Address;
import com.boom.producesyncbe.Data.Role;
import com.boom.producesyncbe.buyerData.Order;
import com.boom.producesyncbe.buyerData.OrderProduct;
import com.boom.producesyncbe.buyerData.Status;
import com.boom.producesyncbe.buyerService.BuyerService;
import com.boom.producesyncbe.commonutils.HelperFunction;
import com.boom.producesyncbe.repository.AddressRepository;
import com.boom.producesyncbe.repository.OrderRepository;
import com.boom.producesyncbe.repository.ProductRepository;
import com.boom.producesyncbe.sellerData.Product;
import com.boom.producesyncbe.service.AutoIncrementService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AutoIncrementService autoIncrementService;

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
        if (openOrderList.size() == 0) {
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
}
