package com.boom.producesyncbe.buyerService.impl;

public class QuantityExceededException extends RuntimeException {
    public QuantityExceededException(String message) {
        super(message);
    }
}