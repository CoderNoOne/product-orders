package com.app.infrastructure.controller;

import com.app.ProductOrdersApplication;
import com.app.domain.entity.Producer;
import com.app.domain.entity.Product;
import com.app.domain.entity.Shop;
import com.app.domain.entity.Stock;
import com.app.infrastructure.dto.ProducerDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.ShopDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
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
class ShopControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(statements = "insert into shops (name) values ('Samsung'), ('Media Markt')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "delete from shops", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("getAll shops")
    void test1() throws Exception {

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/shops")
                .with(user("user").password("pass").roles("USER_CUSTOMER")
                ))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<List<ShopDto>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertAll(
                () -> assertThat(result.getData(), hasSize(2)),
                () -> assertThat(result.getData(), hasItem(Matchers.hasProperty("name", Matchers.is("Samsung")))),
                () -> assertThat(result.getData(), hasItem(Matchers.hasProperty("name", Matchers.is("Media Markt"))))
        );
    }

    @Test
    @DisplayName("getAll shops - access denied")
    void test2() throws Exception {

        //given
        var expectedExceptionMessage = "Full authentication is required to access this resource";

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/shops"))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<List<ShopDto>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("getAll shops with productInStore parameter - successful")
    @Transactional
    void test3() throws Exception {

        //given

        Product product = Product.builder().name("y510p").price(new BigDecimal("3000")).build();
        Shop shop = Shop.builder().name("Media Markt").build();
        Stock stock = Stock.builder().productsQuantity(Map.of(product, 10)).shop(shop).build();
        shop.setStocks(Set.of(stock));
        Producer producer = Producer.builder().name("Lenovo").products(Set.of(product)).build();
        product.setProducer(producer);

        entityManager.persist(shop);
        entityManager.persist(product);
        entityManager.persist(stock);
        entityManager.persist(producer);


        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/shops")
                .with(user("user").password("pass").roles("USER_CUSTOMER"))
                .param("productInStore", product.getId().toString()))
                .andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<List<ShopDto>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertAll(
                () -> assertThat(result.getData(), hasSize(1)),
                () -> assertThat(result.getData(), hasItem(Matchers.hasProperty("name", Matchers.is("Media Markt")))),
                () -> assertThat(result.getData(), hasItem(Matchers.hasProperty("name", Matchers.is("Media Markt"))))
        );
    }

    @Test
    @DisplayName("getAll shops with productInStore parameter - no product with id - exception should be thrown")
    @Transactional
    void test4() throws Exception {

        //given
        Long id = 123L;
        var expectedExceptionMessage = "No product with id: " + id;

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/shops")
                .with(user("user").password("pass").roles("USER_CUSTOMER"))
                .param("productInStore", id.toString()))
                .andExpect(status().is(404))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<List<ShopDto>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

       assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));
    }

}