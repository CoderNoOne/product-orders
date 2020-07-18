package com.app.infrastructure.controller;

import com.app.ProductOrdersApplication;
import com.app.domain.entity.*;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.infrastructure.dto.AddProductToStockDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.createShop.ProductInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=2222",
        classes = ProductOrdersApplication.class
)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
class StockControllerIntegrationTest {


    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("add product to stock - access denied")
    void test1() throws Exception {

        //given
        var expectedExceptionMessage = "Access is denied";

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/stocks/products")
                .with(user("user").password("pass").roles("USER_CUSTOMER")))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("add product to stock - validation errors")
    void test2() throws Exception {

        //given
        var expectedExceptionMessage = "Validations errors: [Stock id:is null, ProductInfo object:is null]";
        AddProductToStockDto addProductToStockDto = AddProductToStockDto.builder().quantity(10).build();

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/stocks/products")
                .with(user("user").password("pass").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(addProductToStockDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("add product to stock - No value for X property found. Cannot add product. Try again later")
    @Transactional
    void test3() throws Exception {

        //given
        Producer producer = Producer.builder().name("Lenovo").build();
        Product product = Product.builder().name("y510p").producer(producer).build();
        producer.setProducts(Set.of(product));
        Stock stock = Stock.builder().productsQuantity(new HashMap<>()).build();

        entityManager.persist(producer);
        entityManager.persist(product);
        entityManager.persist(stock);


        Long stockId = 2L;
        var expectedExceptionMessage = "No value for X property found. Cannot add product. Try again later";
        AddProductToStockDto addProductToStockDto = AddProductToStockDto.builder()
                .quantity(10)
                .stockId(stock.getId())
                .productInfo(ProductInfo.builder()
                        .name("y510p")
                        .producerName("Lenovo")
                        .build())
                .build();

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/stocks/products")
                .with(user("user").password("pass").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(addProductToStockDto)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("add product to stock - budget limit")
    @Transactional
    void test4() throws Exception {

        //given
        Producer producer = Producer.builder().name("Lenovo").build();
        Product product = Product.builder().name("y510p").price(new BigDecimal("3000")).producer(producer).build();
        producer.setProducts(Set.of(product));
        Shop shop = Shop.builder().name("Media markt").budget(new BigDecimal("0")).build();
        Stock stock = Stock.builder().productsQuantity(new HashMap<>()).shop(shop).build();
        shop.setStocks(Set.of(stock));
        AdminShopProperty adminShopProperty = AdminShopProperty.builder().property(AdminShopPropertyName.X).value(new BigDecimal("0.2")).build();

        entityManager.persist(producer);
        entityManager.persist(product);
        entityManager.persist(shop);
        entityManager.persist(stock);
        entityManager.persist(adminShopProperty);

        var expectedExceptionMessage = "Cannot add products to stock - budget limit";
        AddProductToStockDto addProductToStockDto = AddProductToStockDto.builder()
                .quantity(10)
                .stockId(stock.getId())
                .productInfo(ProductInfo.builder()
                        .name("y510p")
                        .producerName("Lenovo")
                        .build())
                .build();

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/stocks/products")
                .with(user("user").password("pass").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(addProductToStockDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("add product to stock - successful")
    @Transactional
    void test5() throws Exception {

        //given
        Producer producer = Producer.builder().name("Lenovo").build();
        Product product = Product.builder().name("y510p").price(new BigDecimal("3000")).producer(producer).build();
        producer.setProducts(Set.of(product));
        Shop shop = Shop.builder().name("Media markt").budget(new BigDecimal("10000000")).build();
        Stock stock = Stock.builder().productsQuantity(new HashMap<>()).shop(shop).build();
        shop.setStocks(Set.of(stock));
        AdminShopProperty adminShopProperty = AdminShopProperty.builder().property(AdminShopPropertyName.X).value(new BigDecimal("0.2")).build();

        entityManager.persist(producer);
        entityManager.persist(product);
        entityManager.persist(shop);
        entityManager.persist(stock);
        entityManager.persist(adminShopProperty);

        AddProductToStockDto addProductToStockDto = AddProductToStockDto.builder()
                .quantity(10)
                .stockId(stock.getId())
                .productInfo(ProductInfo.builder()
                        .name("y510p")
                        .producerName("Lenovo")
                        .build())
                .build();

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/stocks/products")
                .with(user("user").password("pass").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(addProductToStockDto)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(nullValue()));
        assertThat(result.getData(), is(notNullValue()));

    }
}