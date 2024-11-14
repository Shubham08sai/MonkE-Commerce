package com.monkcommerce.service;

import com.monkcommerce.dao.CouponRepository;
import com.monkcommerce.enities.*;
import com.monkcommerce.exceptions.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CouponService {


    @Autowired
    CouponRepository couponRepository;

    public Coupon saveCoupon(Coupon coupon){
        return couponRepository.save(coupon);
    }


    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon getCouponsById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(()-> new CouponNotFoundException("Coupon not found with id: " + id));
    }


    public Coupon updateCoupon(Long id, Coupon coupon) {

        Coupon exitingCoupon = couponRepository.findById(id)
                .orElseThrow(()-> new CouponNotFoundException("Coupon not found with id" +id));

        exitingCoupon.setType(coupon.getType());
        exitingCoupon.setConditions(coupon.getConditions());
        exitingCoupon.setDiscountDetails(coupon.getDiscountDetails());
        exitingCoupon.setExpirationDate(coupon.getExpirationDate());

        return couponRepository.save(exitingCoupon);

    }

    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id: " + id));
        couponRepository.delete(coupon);
       }

    public List<CouponDiscountResponse> getApplicableCoupons(Cart cart) {
        List<Coupon> allCoupons = couponRepository.findAll();
        List<CouponDiscountResponse> applicableCoupons = new ArrayList<>();

        for (Coupon coupon : allCoupons) {
            // Check if the coupon is applicable to the provided cart
            if (isCouponApplicable(coupon, cart)) {
                // Calculate the discount for this coupon based on the cart
                double discount = calculateDiscount(coupon, cart);
                // Add to the response list
                applicableCoupons.add(new CouponDiscountResponse(coupon.getId(), discount));
            }
        }
        return applicableCoupons;
    }



    private boolean isCouponApplicable(Coupon coupon, Cart cart) {
        // Implement your logic for different coupon types
        // For example, check if cart total is above a threshold for cart-wise coupons,
        // or check if required products are in the cart for product-wise coupons.
        if (coupon.getType().equals("cart-wise")) {
            return cart.getTotalAmount() > coupon.getConditions().getMinCartValue();
        } else if (coupon.getType().equals("product-wise")) {
           /* return cart.containsProduct(coupon.getConditions().getRequiredProduct());*/
            long requiredProductId;
            try {
                requiredProductId = Long.parseLong(coupon.getConditions().getRequiredProduct());
            } catch (NumberFormatException e) {
                // Handle the case where parsing fails (e.g., log an error, skip, or return a response)
                System.out.println("Invalid requiredProduct ID: " + coupon.getConditions().getRequiredProduct());
                // Decide how to handle this error, for example, return false if invalid ID
                return false;
            }

            return cart.containsProduct(requiredProductId);
        } else if (coupon.getType().equals("BxGy")) {
            // Implement BxGy logic based on the buy and get arrays in conditions
            return checkBxGyCondition(coupon, cart);
        }
        return false;
    }


    // Helper method to calculate discount based on coupon type and cart
    private double calculateDiscount(Coupon coupon, Cart cart) {
        double discount = 0.0;

        if (coupon.getType().equals("cart-wise")) {
            // Cart-wise discount calculation
            discount = cart.getTotalAmount() * coupon.getDiscountDetails().getPercentage() / 100;
        } else if (coupon.getType().equals("product-wise")) {
            // Product-wise discount calculation
            discount = cart.getDiscountForProduct(coupon.getConditions().getRequiredProduct(),coupon.getDiscountDetails().getPercentage());
        } else if (coupon.getType().equals("BxGy")) {
            // Calculate discount for BxGy type coupons
            discount = calculateBxGyDiscount(coupon, cart);
        }
        return discount;
    }

    public Cart applyCoupon(Long couponId, Cart cart) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        switch (coupon.getType()) {
            case "cart-wise":
                return applyCartWiseCoupon(cart, coupon);
            case "product-wise":
                return applyProductWiseCoupon(cart, coupon);
            case "BxGy":
                return applyBxGyCoupon(cart, coupon);
            default:
                throw new IllegalArgumentException("Invalid coupon type: " + coupon.getType());
        }
    }




    private Cart applyBxGyCoupon(Cart cart, Coupon coupon) {
        Conditions conditions = coupon.getConditions();
        int buyQtyRequired = conditions.getRequiredBuyQuantity();
        int freeQty = conditions.getFreeGetQuantity();
        int limit = conditions.getRepetitionLimit() != null ? conditions.getRepetitionLimit() : Integer.MAX_VALUE;

        double discount = 0.0;
        int totalApplicableFreeItems = 0;

        for (CartItem item : cart.getItems()) {
            if (conditions.getBuyProductIds().contains(item.getProductId())) {
                int applicableSets = Math.min(item.getQuantity() / buyQtyRequired, limit);
                totalApplicableFreeItems += applicableSets * freeQty;
            }
        }

        // Calculate discount based on applicable free items
        for (CartItem item : cart.getItems()) {
            if (conditions.getGetProductIds().contains(item.getProductId())) {
                int freeItemsToApply = Math.min(item.getQuantity(), totalApplicableFreeItems);
                discount += freeItemsToApply * item.getPrice();
                totalApplicableFreeItems -= freeItemsToApply;
            }
        }

        cart.applyDiscount(discount);
        return cart;
    }


    private Cart applyProductWiseCoupon(Cart cart, Coupon coupon) {
        String requiredProduct = coupon.getConditions().getRequiredProduct();
        double discountPercentage = coupon.getDiscountDetails().getPercentage();

        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(requiredProduct)) {
                double discountAmount = item.getPrice() * (discountPercentage / 100);
                item.setPrice(item.getPrice() - discountAmount);
            }
        }

        return cart;
    }


    private boolean checkBxGyCondition(Coupon coupon, Cart cart) {
        List<String> buyProducts = coupon.getConditions().getBuyProducts();
        List<String> getProducts = coupon.getConditions().getGetProducts();
        int requiredBuyQuantity = coupon.getConditions().getRequiredBuyQuantity();
        int freeGetQuantity = coupon.getConditions().getFreeGetQuantity();
        int repetitionLimit = coupon.getConditions().getRepetitionLimit();

        int buyCount = 0;
        int getCount = 0;

        for (CartItem item : cart.getItems()) {
            if (buyProducts.contains(item.getProductId())) {
                buyCount += item.getQuantity();
            }
            if (getProducts.contains(item.getProductId())) {
                getCount += item.getQuantity();
            }
        }

        int maxApplicableTimes = Math.min(buyCount / requiredBuyQuantity, repetitionLimit);
        return maxApplicableTimes > 0 && getCount >= maxApplicableTimes * freeGetQuantity;
    }
  private double calculateBxGyDiscount(Coupon coupon, Cart cart) {
        List<String> getProducts = coupon.getConditions().getGetProductIds();
        int freeGetQuantity = coupon.getConditions().getFreeGetQuantity();
        int repetitionLimit = coupon.getConditions().getRepetitionLimit();

        int buyCount = 0;
        int getCount = 0;
        double discount = 0.0;

        for (CartItem item : cart.getItems()) {
            if (coupon.getConditions().getBuyProductIds().contains(item.getProductId())) {
                buyCount += item.getQuantity();
            }
        }

        int maxApplicableTimes = Math.min(buyCount / coupon.getConditions().getRequiredBuyQuantity(), repetitionLimit);

        for (CartItem item : cart.getItems()) {
            if (getProducts.contains(item.getProductId()) && getCount < maxApplicableTimes * freeGetQuantity) {
                int eligibleFreeQty = Math.min(item.getQuantity(), maxApplicableTimes * freeGetQuantity - getCount);
                discount += eligibleFreeQty * item.getPrice();
                getCount += eligibleFreeQty;
            }
        }

        return discount;
    }



    private Cart applyCartWiseCoupon(Cart cart, Coupon coupon) {
        double discount = 0.0;

        // Check if cart total meets the minimum requirement for the coupon
        if (cart.getTotalAmount() > coupon.getConditions().getMinCartValue()) {
            discount = cart.getTotalAmount() * (coupon.getDiscountDetails().getPercentage() / 100);
            cart.applyDiscount(discount); // Apply discount directly to the cart
        }

        return cart; // Return the updated cart with the applied discount
    }

}


