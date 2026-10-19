package com.geli.shopinventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geli.shopinventory.domain.Item;
import com.geli.shopinventory.domain.ItemVariant;
import com.geli.shopinventory.dto.CheckoutRequest;
import com.geli.shopinventory.dto.ItemRequest;
import com.geli.shopinventory.dto.OrderItemRequest;
import com.geli.shopinventory.repository.ItemRepository;
import com.geli.shopinventory.repository.ItemVariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ShopInventoryIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemVariantRepository variantRepository;

    private Item testItem;
    private ItemVariant inStockVariant;
    private ItemVariant outOfStockVariant;

    @BeforeEach
    void setUp() {
        testItem = Item.builder()
                .code("MOCK-HOODIE")
                .name("Mock Pullover Hoodie")
                .description("Warm fleece hoodie")
                .category("Apparel")
                .active(true)
                .build();

        inStockVariant = ItemVariant.builder()
                .sku("HOODIE-M")
                .variantName("Medium Grey")
                .price(new BigDecimal("250000.00"))
                .stockQuantity(5)
                .active(true)
                .build();

        outOfStockVariant = ItemVariant.builder()
                .sku("HOODIE-XL")
                .variantName("XL Grey (Sold Out)")
                .price(new BigDecimal("270000.00"))
                .stockQuantity(0)
                .active(true)
                .build();

        testItem.addVariant(inStockVariant);
        testItem.addVariant(outOfStockVariant);
        itemRepository.save(testItem);
    }

    @Test
    @DisplayName("GET /api/v1/items should return list of items with status 200")
    void testGetAllItems() throws Exception {
        mockMvc.perform(get("/api/v1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[?(@.code == 'MOCK-HOODIE')]").exists());
    }

    @Test
    @DisplayName("POST /api/v1/items should create new item with status 201")
    void testCreateItem() throws Exception {
        ItemRequest request = new ItemRequest("CAP-001", "Snapback Cap", "Adjustable cap", "Accessories", true);

        mockMvc.perform(post("/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("CAP-001")))
                .andExpect(jsonPath("$.name", is("Snapback Cap")));
    }

    @Test
    @DisplayName("POST /api/v1/items with blank code should return 400 Bad Request")
    void testCreateItemValidationFailure() throws Exception {
        ItemRequest request = new ItemRequest("", "", "Desc", "Cat", true);

        mockMvc.perform(post("/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Error")))
                .andExpect(jsonPath("$.invalid_fields", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/orders/checkout should succeed (201) when requested item has enough stock")
    void testCheckoutSuccess() throws Exception {
        CheckoutRequest request = new CheckoutRequest(
                "Michael Scott",
                List.of(new OrderItemRequest(null, "HOODIE-M", 2))
        );

        mockMvc.perform(post("/api/v1/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNo", startsWith("ORD-")))
                .andExpect(jsonPath("$.customerName", is("Michael Scott")))
                .andExpect(jsonPath("$.totalAmount", is(500000.0)))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/v1/orders/checkout should return 422 Unprocessable Entity when buying out-of-stock item")
    void testCheckoutFailsWhenOutOfStock() throws Exception {
        CheckoutRequest request = new CheckoutRequest(
                "Dwight Schrute",
                List.of(new OrderItemRequest(null, "HOODIE-XL", 1)) // stock is 0
        );

        mockMvc.perform(post("/api/v1/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title", is("Out of Stock / Insufficient Inventory")))
                .andExpect(jsonPath("$.detail", containsString("Insufficient stock for SKU 'HOODIE-XL'")));
    }

    @Test
    @DisplayName("GET /api/v1/stocks/out-of-stock should include zero-stock variant")
    void testGetOutOfStockEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/stocks/out-of-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.sku == 'HOODIE-XL')]").exists());
    }
}

