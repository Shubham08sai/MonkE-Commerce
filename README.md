Coupon Management API - README

This document provides a comprehensive overview of the Coupon Management API, which handles different types of discount coupons for an e-commerce platform. The API allows for managing and applying various discount types such as cart-wise, product-wise, and Buy X Get Y (BxGy) coupons. This README covers key use cases, implementation details, assumptions, and limitations of the current solution.



Table of Contents
- [Introduction]
- [Key Features]
- [Use Cases]
- [Implementation Details]
- [Assumptions and Limitations]
- [Setup Instructions]


Introduction
The Coupon Management API is designed to apply different discount types to a customer's cart based on the coupon type. The API supports cart-wide discounts, product-specific discounts, and "Buy X Get Y Free" (BxGy) coupons. It is built using Java with Spring Boot and integrates with an e-commerce application to manage cart data and coupons.

---
 Key Features
- Cart-Wide Discount: Discounts applied to the entire cart based on a minimum cart value.
- Product-Specific Discount: Discounts applied to specific products in the cart.
- Buy X Get Y Free (BxGy) Coupon: Provides a free quantity of items when a required quantity is purchased.

---

Use Cases

 Implemented Use Cases
1. Cart-Wide Discount: 
   - Applicable if the total cart value exceeds a specified `minCartValue`.
   - Only one cart-wide discount can apply at a time.

2. Product-Specific Discount: 
   - Applicable to specific products in the cart.
   - Requires the product ID to match `requiredProduct` in the coupon conditions.

3. BxGy Coupon:
   - Applicable for offers like "Buy X items, Get Y free".
   - Constraints include a `requiredBuyQuantity` and `freeGetQuantity` fields.
   - Includes `repetitionLimit` for controlling how many times the BxGy coupon can apply.

4. Error Handling and Validation:
   - Validates data types and structures.
   - Handles null values for products, quantities, or prices to prevent runtime errors.

Additional Considered Use Cases (Not Implemented)
1. Product Category-Specific Coupons: Discounts that apply to all items within a certain category (e.g., electronics, clothing).
2. User-Specific Coupons: Coupons that apply only to certain users based on their purchase history or loyalty tier.
3. Time-Based Coupons: Coupons that are only applicable within a specific time window.
4. Multi-BxGy Coupons: More complex combinations of BxGy offers, such as "Buy 3 Get 2 free on product A, Buy 5 Get 1 free on product B".

---

Implementation Details

Code Modifications and Logic

1. Coupon Calculation Logic:
   - `calculateBxGyDiscount`: Calculates discounts for BxGy coupons based on the quantity purchased and applies the `repetitionLimit` if specified.
   - Integration with cart and product details is validated to ensure that only applicable discounts are processed.

2. Error Handling:
   - Ensures that `productId` values are not null.
   - Verifies that `cart.containsProduct()` receives a valid `Long` by converting strings to longs and catching `NumberFormatException` for invalid inputs.

3. Coupon API Endpoints:
   - `POST /coupons/applicable-coupons`: Receives a cart payload and checks applicable coupons.
   - Validates incoming payloads to ensure proper data structure, with checks on `productId`, `quantity`, and `price` fields in `CartItem`.



 Assumptions and Limitations

 Assumptions
- Data Integrity: Assumes all product and cart data provided in requests are valid, especially `productId`, `quantity`, and `price` fields.
- Single Discount Application: Only one coupon of each type (cart-wide, product-specific, or BxGy) can apply at a time, based on configured priorities.
- BxGy Application: Assumes that BxGy coupons are applied sequentially, based on `repetitionLimit` if specified.

 Limitations
- Limited Coupon Types: The API currently supports cart-wide, product-specific, and BxGy coupons only. Other potential coupon types like category-specific or user-based are noted but not implemented.
- No External Dependency Management: Coupons are statically configured and do not integrate with an external coupon management or tracking service.
- Simplified Error Handling: Basic exception handling is included, but there may be cases where more granular error responses are needed for robust production use.



 Setup Instructions

1. Prerequisites
   - Java 17+
   - Maven or Gradle for dependency management
   - An IDE like IntelliJ IDEA or Eclipse

2. Build and Run
   - Clone the repository.
   - Install dependencies using Maven: `mvn install`.
   - Run the application with `mvn spring-boot:run`.
   - Access the API at `http://localhost:8080`.

3. Sample Payload for `/coupons/applicable-coupons
   json
   {
     "cart": {
       "items": [
         {"product_id": 1, "quantity": 6, "price": 50},
         {"product_id": 2, "quantity": 3, "price": 30}
       ]
     }
   }
   

4. Testing and Validation
   - Use tools like Postman to test the API endpoints.
   - Verify error handling by modifying payloads and observing error messages in the response.



This README provides a detailed guide on using and extending the Coupon Management API, along with practical assumptions and potential limitations for enhancement.
