package com.monkcommerce.exceptions;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException() {
        super("Coupon not found !!");
    }
    public CouponNotFoundException(String message) {
        super(message);
    }
}
