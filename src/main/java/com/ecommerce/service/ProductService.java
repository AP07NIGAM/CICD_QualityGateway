package com.ecommerce.service;

import com.ecommerce.model.Product;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ProductService {
    private final Map<String, Product> productCatalog;

    public ProductService() {
        this.productCatalog = new HashMap<>();
        initializeCatalog();
    }

    private void initializeCatalog() {
        // Initialize with sample products
        addProduct(new Product("P001", "Laptop", "High-performance laptop", new BigDecimal("999.99"), 10, "Electronics"));
        addProduct(new Product("P002", "Smartphone", "Latest smartphone", new BigDecimal("699.99"), 25, "Electronics"));
        addProduct(new Product("P003", "Headphones", "Wireless headphones", new BigDecimal("149.99"), 50, "Electronics"));
        addProduct(new Product("P004", "Book", "Programming guide", new BigDecimal("39.99"), 100, "Books"));
        addProduct(new Product("P005", "Mouse", "Wireless mouse", new BigDecimal("29.99"), 75, "Electronics"));
    }

    public void addProduct(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("Product and product ID cannot be null");
        }
        productCatalog.put(product.getId(), product);
    }

    public Product getProductById(String productId) {
        Product product = productCatalog.get(productId);
        if (product == null) {
            throw new NoSuchElementException("Product not found: " + productId);
        }
        return product;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productCatalog.values());
    }

    public List<Product> getProductsByCategory(String category) {
        return productCatalog.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Product> searchProducts(String keyword) {
        return productCatalog.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        p.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void updateProduct(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("Product and product ID cannot be null");
        }
        if (!productCatalog.containsKey(product.getId())) {
            throw new NoSuchElementException("Product not found: " + product.getId());
        }
        productCatalog.put(product.getId(), product);
    }

    public void deleteProduct(String productId) {
        if (!productCatalog.containsKey(productId)) {
            throw new NoSuchElementException("Product not found: " + productId);
        }
        productCatalog.remove(productId);
    }

    public boolean isProductAvailable(String productId, int quantity) {
        Product product = getProductById(productId);
        return product.getStockQuantity() >= quantity;
    }

    public void updateStock(String productId, int quantity) {
        Product product = getProductById(productId);
        product.setStockQuantity(quantity);
    }
}

