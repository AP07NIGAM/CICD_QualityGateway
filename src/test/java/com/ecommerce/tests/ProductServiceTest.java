package com.ecommerce.tests;

import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertThrows;

public class ProductServiceTest {
    private ProductService productService;

    @BeforeMethod
    public void setUp() {
        productService = new ProductService();
    }

    @Test(groups = {"smoke", "regression"}, priority = 1)
    public void testGetProductById_Success() {
        // Critical smoke test - verify basic product retrieval
        Product product = productService.getProductById("P001");
        assertNotNull(product, "Product should not be null");
        assertEquals(product.getId(), "P001");
        assertEquals(product.getName(), "Laptop");
    }

    @Test(groups = {"smoke", "regression"}, priority = 2)
    public void testGetAllProducts_ReturnsProducts() {
        // Critical smoke test - verify catalog is populated
        List<Product> products = productService.getAllProducts();
        assertNotNull(products, "Product list should not be null");
        assertTrue(products.size() > 0, "Product catalog should contain products");
        assertTrue(products.size() >= 5, "Should have at least 5 products");
    }

    @Test(groups = {"regression"})
    public void testGetProductById_NotFound() {
        // Regression test - error handling
        assertThrows(NoSuchElementException.class, () -> {
            productService.getProductById("INVALID");
        });
    }

    @Test(groups = {"regression"})
    public void testAddProduct_Success() {
        Product newProduct = new Product("P999", "Test Product", "Test Description",
                new BigDecimal("99.99"), 10, "Test");
        productService.addProduct(newProduct);

        Product retrieved = productService.getProductById("P999");
        assertNotNull(retrieved);
        assertEquals(retrieved.getName(), "Test Product");
    }

    @Test(groups = {"regression"})
    public void testAddProduct_NullProduct() {
        assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(null);
        });
    }

    @Test(groups = {"regression"})
    public void testUpdateProduct_Success() {
        Product product = productService.getProductById("P001");
        product.setPrice(new BigDecimal("1099.99"));
        productService.updateProduct(product);

        Product updated = productService.getProductById("P001");
        assertEquals(updated.getPrice(), new BigDecimal("1099.99"));
    }

    @Test(groups = {"regression"})
    public void testDeleteProduct_Success() {
        productService.deleteProduct("P005");
        assertThrows(NoSuchElementException.class, () -> {
            productService.getProductById("P005");
        });
    }

    @Test(groups = {"smoke", "regression"}, priority = 3)
    public void testGetProductsByCategory_Electronics() {
        // Critical smoke test - category filtering
        List<Product> electronics = productService.getProductsByCategory("Electronics");
        assertNotNull(electronics);
        assertTrue(electronics.size() > 0, "Should have electronics products");
        electronics.forEach(p -> assertEquals(p.getCategory(), "Electronics"));
    }

    @Test(groups = {"regression"})
    public void testSearchProducts_ByName() {
        List<Product> results = productService.searchProducts("laptop");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        assertTrue(results.stream().anyMatch(p -> p.getName().toLowerCase().contains("laptop")));
    }

    @Test(groups = {"regression"})
    public void testSearchProducts_NoResults() {
        List<Product> results = productService.searchProducts("nonexistent");
        assertNotNull(results);
        assertEquals(results.size(), 0);
    }

    @Test(groups = {"smoke", "regression"}, priority = 4)
    public void testIsProductAvailable_InStock() {
        // Critical smoke test - stock availability check
        boolean available = productService.isProductAvailable("P001", 5);
        assertTrue(available, "Product should be available");
    }

    @Test(groups = {"regression"})
    public void testIsProductAvailable_OutOfStock() {
        boolean available = productService.isProductAvailable("P001", 1000);
        assertFalse(available, "Product should not be available in requested quantity");
    }

    @Test(groups = {"regression"})
    public void testUpdateStock_Success() {
        productService.updateStock("P001", 20);
        Product product = productService.getProductById("P001");
        assertEquals(product.getStockQuantity(), 20);
    }
}

