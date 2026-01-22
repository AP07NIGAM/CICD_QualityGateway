package com.ecommerce.tests;

import com.ecommerce.model.Product;
import com.ecommerce.model.ShoppingCart;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertThrows;

public class ShoppingCartTest {
    private ShoppingCart cart;
    private Product product1;
    private Product product2;

    @BeforeMethod
    public void setUp() {
        cart = new ShoppingCart("user123");
        product1 = new Product("P001", "Laptop", "High-performance laptop",
                new BigDecimal("999.99"), 10, "Electronics");
        product2 = new Product("P002", "Mouse", "Wireless mouse",
                new BigDecimal("29.99"), 50, "Electronics");
    }

    @Test(groups = {"smoke", "regression"}, priority = 1)
    public void testAddItemToCart_Success() {
        // Critical smoke test - add item to cart
        cart.addItem(product1, 1);
        assertEquals(cart.getItemCount(), 1, "Cart should have 1 item");
        assertFalse(cart.isEmpty(), "Cart should not be empty");
    }

    @Test(groups = {"smoke", "regression"}, priority = 2)
    public void testGetCartTotal_SingleItem() {
        // Critical smoke test - calculate cart total
        cart.addItem(product1, 2);
        BigDecimal expectedTotal = new BigDecimal("999.99").multiply(BigDecimal.valueOf(2));
        assertEquals(cart.getTotal(), expectedTotal, "Cart total should be correct");
    }

    @Test(groups = {"smoke", "regression"}, priority = 3)
    public void testGetCartTotal_MultipleItems() {
        // Critical smoke test - multiple items total
        cart.addItem(product1, 1);
        cart.addItem(product2, 2);

        BigDecimal expectedTotal = new BigDecimal("999.99")
                .add(new BigDecimal("29.99").multiply(BigDecimal.valueOf(2)));
        assertEquals(cart.getTotal(), expectedTotal, "Cart total should include all items");
    }

    @Test(groups = {"regression"})
    public void testAddItemToCart_NullProduct() {
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addItem(null, 1);
        });
    }

    @Test(groups = {"regression"})
    public void testAddItemToCart_OutOfStock() {
        Product outOfStock = new Product("P999", "Out of Stock", "Test",
                new BigDecimal("99.99"), 0, "Test");
        assertThrows(IllegalStateException.class, () -> {
            cart.addItem(outOfStock, 1);
        });
    }

    @Test(groups = {"regression"})
    public void testAddItemToCart_ExceedsStock() {
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addItem(product1, 100);
        });
    }

    @Test(groups = {"regression"})
    public void testAddItemToCart_IncrementExisting() {
        cart.addItem(product1, 1);
        cart.addItem(product1, 2);

        assertEquals(cart.getItemCount(), 3, "Should have 3 items of same product");
        assertEquals(cart.getItems().size(), 1, "Should have 1 unique product");
    }

    @Test(groups = {"smoke", "regression"}, priority = 4)
    public void testRemoveItemFromCart_Success() {
        // Critical smoke test - remove item
        cart.addItem(product1, 1);
        cart.addItem(product2, 1);

        cart.removeItem("P001");
        assertEquals(cart.getItemCount(), 1, "Should have 1 item after removal");
    }

    @Test(groups = {"regression"})
    public void testUpdateItemQuantity_Success() {
        cart.addItem(product1, 1);
        cart.updateItemQuantity("P001", 3);

        assertEquals(cart.getItemCount(), 3, "Quantity should be updated");
    }

    @Test(groups = {"regression"})
    public void testUpdateItemQuantity_ExceedsStock() {
        cart.addItem(product1, 1);
        assertThrows(IllegalArgumentException.class, () -> {
            cart.updateItemQuantity("P001", 100);
        });
    }

    @Test(groups = {"regression"})
    public void testUpdateItemQuantity_ProductNotInCart() {
        assertThrows(IllegalArgumentException.class, () -> {
            cart.updateItemQuantity("P999", 1);
        });
    }

    @Test(groups = {"regression"})
    public void testClearCart_Success() {
        cart.addItem(product1, 1);
        cart.addItem(product2, 1);

        cart.clear();
        assertTrue(cart.isEmpty(), "Cart should be empty after clear");
        assertEquals(cart.getTotal(), BigDecimal.ZERO, "Total should be zero");
    }

    @Test(groups = {"regression"})
    public void testGetItemCount_MultipleProducts() {
        cart.addItem(product1, 2);
        cart.addItem(product2, 3);

        assertEquals(cart.getItemCount(), 5, "Total item count should be 5");
    }
}

