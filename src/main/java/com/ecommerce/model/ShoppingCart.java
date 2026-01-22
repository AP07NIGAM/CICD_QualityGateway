package com.ecommerce.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShoppingCart {
    private String userId;
    private List<CartItem> items;

    public ShoppingCart(String userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public void addItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (!product.isInStock()) {
            throw new IllegalStateException("Product is out of stock");
        }
        if (quantity > product.getStockQuantity()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock");
        }

        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (newQuantity > product.getStockQuantity()) {
                throw new IllegalArgumentException("Total quantity exceeds available stock");
            }
            item.setQuantity(newQuantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    public void removeItem(String productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void updateItemQuantity(String productId, int quantity) {
        CartItem item = items.stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        if (quantity > item.getProduct().getStockQuantity()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock");
        }
        item.setQuantity(quantity);
    }

    public void clear() {
        items.clear();
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemCount() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}

