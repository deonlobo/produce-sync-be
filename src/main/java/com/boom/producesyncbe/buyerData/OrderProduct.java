package com.boom.producesyncbe.buyerData;

import org.springframework.data.annotation.Id;

import java.util.List;

public class OrderProduct {
    @Id
    private String productId;
    private String sellerId;
    private String productName;
    private String productDescription;
    private List<String> productImages;
    private Integer quantity;
    private Double perUnitPrice;
    private String unit;
    private Double total;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPerUnitPrice() {
        return perUnitPrice;
    }

    public void setPerUnitPrice(Double perUnitPrice) {
        this.perUnitPrice = perUnitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
