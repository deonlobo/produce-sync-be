package com.boom.producesyncbe.buyerData;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document
public class Order {
    @Id
    private String orderId;
    private String buyerId;
    private String sellerId;
    private String brandName;
    private List<OrderProduct> productList = new ArrayList<>();
    private Double orderTotal;
    private Status status;
    private long createdTs = Instant.now().toEpochMilli();
    private long updatedTs = Instant.now().toEpochMilli();
    @Transient
    private List<String> productQtyExceeded = new ArrayList<>();

    public List<String> getProductQtyExceeded() {
        return productQtyExceeded;
    }

    public void setProductQtyExceeded(List<String> productQtyExceeded) {
        this.productQtyExceeded = productQtyExceeded;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public List<OrderProduct> getProductList() {
        return productList;
    }

    public void setProductList(List<OrderProduct> productList) {
        this.productList = productList;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(long createdTs) {
        this.createdTs = createdTs;
    }

    public long getUpdatedTs() {
        return updatedTs;
    }

    public void setUpdatedTs(long updatedTs) {
        this.updatedTs = updatedTs;
    }
}
