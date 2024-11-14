package com.monkcommerce.enities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDiscountResponse {
    private Long couponId;
    private double discount;
}
