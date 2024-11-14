package com.monkcommerce.enities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    private double totalAmount;
    private List<CartItem> items = new ArrayList<>();



    public boolean containsProduct(Long productId) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        for (CartItem item : items) {
            // Ensure item and productId are not null before calling equals
            if (item.getProductId() != null && item.getProductId().equals(productId)) {
                return true;
            }
        }
        return false;
    }


    // Calculate a discount for a specific product in the cart
    public double getDiscountForProduct(String productId, double percentage) {
        for (CartItem item : items) {
            if (item.getProductId().equals(productId)) {
                return item.getPrice() * item.getQuantity() * (percentage / 100);
            }
        }
        return 0.0;
    }

    public List<CartItem> getItems() {
        return items;
    }

    // Add a method to apply a discount to the total amount
    public void applyDiscount(double discount) {
        this.totalAmount -= discount;
    }
}
