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
public class Conditions {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double minCartValue; // for cart-wise coupon minimum cart value
    private String requiredProduct; // for product-wise coupon required product in cart
    @ElementCollection
    private List<String> buyProducts;

    @ElementCollection
    private List<String> getProducts;

    // Fields for BxGy conditions
    private Integer requiredBuyQuantity; // Number of "buy" products required
    private Integer freeGetQuantity; // Number of "get" products to be free
    private Integer repetitionLimit; // Max times the BxGy coupon can be applied

    public Double getMinCartValue() { return minCartValue; }

    public String getRequiredProduct() { return requiredProduct; }

    // Method to retrieve the "buy" products list
    public List<String> getBuyProductIds() { return buyProducts; }

    public void setBuyProducts(List<String> buyProducts) { this.buyProducts = buyProducts; }

    // Method to retrieve the "get" products list
    public List<String> getGetProductIds() { return getProducts; }

    public Integer getRequiredBuyQuantity() { return requiredBuyQuantity; }

    public Integer getFreeGetQuantity() { return freeGetQuantity; }

    public Integer getRepetitionLimit() { return repetitionLimit; }
}
