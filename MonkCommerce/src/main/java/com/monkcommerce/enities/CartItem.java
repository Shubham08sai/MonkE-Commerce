package com.monkcommerce.enities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @JsonProperty("product_id")
    private String productId;
    private double price;
    private int quantity;
}