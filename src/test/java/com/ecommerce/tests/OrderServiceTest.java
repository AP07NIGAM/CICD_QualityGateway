package com.ecommerce.tests;

import com.ecommerce.model.Order;
import com.ecommerce.model.Product;
import com.ecommerce.model.ShoppingCart;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertThrows;

public class OrderServiceTest {
    private OrderService orderService;
    private ProductService productService;
    private ShoppingCart cart;

    @BeforeMethod
    public void setUp() {
        productService = new ProductService();
        orderService = new OrderService(productService);
        cart = new ShoppingCart("user123");
    }

    @Test(groups = {"smoke", "regression"}, priority = 1)
    public void testCreateOrder_Success() {
        // Critical smoke test - order creation
        Product product = productService.getProductById("P001");
        cart.addItem(product, 1);

        Order order = orderService.createOrder(cart, "123 Main St");

        assertNotNull(order, "Order should be created");
        assertNotNull(order.getOrderId(), "Order ID should be generated");
        assertEquals(order.getStatus(), Order.OrderStatus.PENDING, "Order should be pending");
        assertEquals(order.getUserId(), "user123");
    }

    @Test(groups = {"smoke", "regression"}, priority = 2)
    public void testCreateOrder_ReducesStock() {
        // Critical smoke test - stock reduction on order
        Product product = productService.getProductById("P002");
        int initialStock = product.getStockQuantity();
        cart.addItem(product, 2);

        orderService.createOrder(cart, "123 Main St");

        assertEquals(product.getStockQuantity(), initialStock - 2, "Stock should be reduced");
    }

    @Test(groups = {"regression"})
    public void testCreateOrder_EmptyCart() {
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(cart, "123 Main St");
        });
    }

    @Test(groups = {"regression"})
    public void testCreateOrder_NoShippingAddress() {
        Product product = productService.getProductById("P001");
        cart.addItem(product, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(cart, "");
        });
    }

    @Test(groups = {"regression"})
    public void testCreateOrder_InsufficientStock() {
        Product product = productService.getProductById("P001");
        productService.updateStock("P001", 2);

        // Add 2 items to first cart
        cart.addItem(product, 2);

        // Create first order to reduce stock to 0
        orderService.createOrder(cart, "123 Main St");

        // Try to add item to cart when stock is 0 - should fail at cart level
        ShoppingCart cart2 = new ShoppingCart("user456");

        assertThrows(IllegalStateException.class, () -> {
            cart2.addItem(product, 1);
        });
    }

    @Test(groups = {"smoke", "regression"}, priority = 3)
    public void testGetOrderById_Success() {
        // Critical smoke test - order retrieval
        Product product = productService.getProductById("P001");
        cart.addItem(product, 1);
        Order createdOrder = orderService.createOrder(cart, "123 Main St");

        Order retrievedOrder = orderService.getOrderById(createdOrder.getOrderId());
        assertNotNull(retrievedOrder);
        assertEquals(retrievedOrder.getOrderId(), createdOrder.getOrderId());
    }

    @Test(groups = {"regression"})
    public void testGetOrderById_NotFound() {
        assertThrows(NoSuchElementException.class, () -> {
            orderService.getOrderById("INVALID");
        });
    }

    @Test(groups = {"smoke", "regression"}, priority = 4)
    public void testConfirmOrder_Success() {
        // Critical smoke test - order workflow
        Product product = productService.getProductById("P001");
        cart.addItem(product, 1);
        Order order = orderService.createOrder(cart, "123 Main St");

        orderService.confirmOrder(order.getOrderId());
        assertEquals(order.getStatus(), Order.OrderStatus.CONFIRMED);
    }

    @Test(groups = {"regression"})
    public void testShipOrder_Success() {
        Product product = productService.getProductById("P001");
        cart.addItem(product, 1);
        Order order = orderService.createOrder(cart, "123 Main St");

        orderService.confirmOrder(order.getOrderId());
        orderService.shipOrder(order.getOrderId());
        assertEquals(order.getStatus(), Order.OrderStatus.SHIPPED);
    }

    @Test(groups = {"regression"})
    public void testDeliverOrder_Success() {
        Product product = productService.getProductById("P001");
        cart.addItem(product, 1);
        Order order = orderService.createOrder(cart, "123 Main St");

        orderService.confirmOrder(order.getOrderId());
        orderService.shipOrder(order.getOrderId());
        orderService.deliverOrder(order.getOrderId());
        assertEquals(order.getStatus(), Order.OrderStatus.DELIVERED);
    }

    @Test(groups = {"regression"})
    public void testCancelOrder_RestoresStock() {
        Product product = productService.getProductById("P003");
        int initialStock = product.getStockQuantity();
        cart.addItem(product, 3);
        Order order = orderService.createOrder(cart, "123 Main St");

        orderService.cancelOrder(order.getOrderId());

        assertEquals(order.getStatus(), Order.OrderStatus.CANCELLED);
        assertEquals(product.getStockQuantity(), initialStock, "Stock should be restored");
    }

    @Test(groups = {"regression"})
    public void testGetOrdersByUserId_Success() {
        Product product = productService.getProductById("P001");
        cart.addItem(product, 1);
        orderService.createOrder(cart, "123 Main St");

        cart.clear();
        cart.addItem(product, 1);
        orderService.createOrder(cart, "456 Oak Ave");

        List<Order> userOrders = orderService.getOrdersByUserId("user123");
        assertEquals(userOrders.size(), 2, "Should have 2 orders for user");
    }

    @Test(groups = {"regression"})
    public void testGetAllOrders_Success() {
        Product product = productService.getProductById("P001");
        cart.addItem(product, 1);
        orderService.createOrder(cart, "123 Main St");

        List<Order> allOrders = orderService.getAllOrders();
        assertTrue(allOrders.size() > 0, "Should have at least 1 order");
    }
}

