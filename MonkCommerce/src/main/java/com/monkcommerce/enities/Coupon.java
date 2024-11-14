package com.monkcommerce.enities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type; // e.g., "cart-wise", "product-wise", "BxGy"
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "discount_details_id")
    private DiscountDetails discountDetails;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "conditions_id")
    private Conditions conditions;
    private LocalDateTime expirationDate; // Date for expiration
    // Additional fields based on type and requirements
}
