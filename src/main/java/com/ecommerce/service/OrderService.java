package com.ecommerce.service;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.Product;
import com.ecommerce.model.ShoppingCart;

import java.util.*;

public class OrderService {
    private final Map<String, Order> orders;
    private final ProductService productService;
    private int orderCounter = 1;

    public OrderService(ProductService productService) {
        this.orders = new HashMap<>();
        this.productService = productService;
    }

    public Order createOrder(ShoppingCart cart, String shippingAddress) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from empty cart");
        }
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping address is required");
        }

        // Validate stock availability
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (!productService.isProductAvailable(product.getId(), item.getQuantity())) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
        }

        // Create order
        String orderId = generateOrderId();
        Order order = new Order(orderId, cart.getUserId(), cart.getItems(), shippingAddress);
        orders.put(orderId, order);

        // Reduce stock
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            product.reduceStock(item.getQuantity());
        }

        return order;
    }

    public Order getOrderById(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new NoSuchElementException("Order not found: " + orderId);
        }
        return order;
    }

    public List<Order> getOrdersByUserId(String userId) {
        List<Order> userOrders = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getUserId().equals(userId)) {
                userOrders.add(order);
            }
        }
        return userOrders;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public void confirmOrder(String orderId) {
        Order order = getOrderById(orderId);
        order.confirm();
    }

    public void shipOrder(String orderId) {
        Order order = getOrderById(orderId);
        order.ship();
    }

    public void deliverOrder(String orderId) {
        Order order = getOrderById(orderId);
        order.deliver();
    }

    public void cancelOrder(String orderId) {
        Order order = getOrderById(orderId);
        order.cancel();

        // Restore stock
        for (CartItem item : order.getItems()) {
            Product product = item.getProduct();
            product.addStock(item.getQuantity());
        }
    }

    private String generateOrderId() {
        return "ORD" + String.format("%06d", orderCounter++);
    }

    public int getOrderCount() {
        return orders.size();
    }
}

