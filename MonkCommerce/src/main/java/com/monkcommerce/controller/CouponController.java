package com.monkcommerce.controller;

import com.monkcommerce.enities.Cart;
import com.monkcommerce.enities.Coupon;
import com.monkcommerce.enities.CouponDiscountResponse;
import com.monkcommerce.service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    CouponService couponService;

    private static final Logger logger = LoggerFactory.getLogger(CouponController.class);


    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        couponService.saveCoupon(coupon);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCouponsById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.updateCoupon(id,coupon));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/applicable-coupons")
    public ResponseEntity<List<CouponDiscountResponse>> getApplicableCoupons(@RequestBody Cart cart) {
        logger.info("Received request to get applicable coupons for cart: {}", cart);
        List<CouponDiscountResponse> applicableCoupons = couponService.getApplicableCoupons(cart);
        logger.info("Applicable coupons found: {}", applicableCoupons);
        return ResponseEntity.ok(applicableCoupons);
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<Cart> applyCouponToCart(@PathVariable Long id, @RequestBody Cart cart) {
        logger.info("Received request to apply coupon with ID {} to cart: {}", id, cart);
        Cart updatedCart = couponService.applyCoupon(id, cart);
        logger.info("Updated cart after applying coupon: {}", updatedCart);
        return ResponseEntity.ok(updatedCart);
    }
}
