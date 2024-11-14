package com.monkcommerce.enities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDetails {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double percentage; // for cart-wise or product-wise discount percentage
    private String product; // for product-wise coupon target product
    private Integer buyCount; // for BxGy
    private Integer getCount; // for BxGy
    private List<String> buyArray; // for BxGy
    private List<String> getArray; // for BxGy
    private Integer repetitionLimit; // for BxGy

}
