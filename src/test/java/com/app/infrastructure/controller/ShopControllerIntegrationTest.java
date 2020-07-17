package com.app.infrastructure.controller;

import com.app.ProductOrdersApplication;
import com.app.domain.entity.*;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.ShopDto;
import com.app.infrastructure.dto.UpdateShopDto;
import com.app.infrastructure.dto.createShop.CreateShopDto;
import com.app.infrastructure.dto.createShop.CreateStockDto;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Collections;
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
class ShopControllerIntegrationTest {

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

    @Test
    @DisplayName("get one - no shop with id")
    void test5() throws Exception {

        //given
        Long id = 123L;
        var expectedExceptionMessage = "No shop with id: " + id;

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/shops/{id}", id)
                .with(user("user").password("pass").roles("ADMIN_SHOP")))
                .andExpect(status().is(404))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<ShopDto> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("get one - access denied")
    void test6() throws Exception {

        //given
        Long id = 123L;
        var expectedExceptionMessage = "Access is denied";

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/shops/{id}", id)
                .with(user("user").password("pass").roles("USER_CUSTOMER")))
                .andExpect(status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<ShopDto> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("get one - successful")
    @Transactional
    void test7() throws Exception {

        //given

        Shop shop = Shop.builder().name("Shop x").build();
        entityManager.persist(shop);

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/shops/{id}", shop.getId())
                .with(user("user").password("pass").roles("ADMIN_SHOP")))
                .andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<ShopDto> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertThat(result.getData(), is(equalTo(shop.toDto())));
    }

    @Test
    @DisplayName("delete - successful")
    @Transactional
    void test8() throws Exception {

        //given
        Shop shop = Shop.builder().name("Samsung").build();
        entityManager.persist(shop);

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{id}", shop.getId())
                .with(user("user").password("pass").roles("ADMIN_SHOP")))
                //then
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("delete - no shop with id")
    void test9() throws Exception {

        //given
        Long id = 2L;
        var expectedExceptionMessage = "No shop with id: " + id;

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{id}", id)
                .with(user("user").password("pass").roles("ADMIN_SHOP")))
                //then
                .andExpect(status().isNotFound())
                .andReturn();

        ResponseData<ShopDto> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("delete shop - access denied")
    void test10() throws Exception {

        //given
        Long id = 2L;
        var expectedExceptionMessage = "Access is denied";

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{id}", id)
                .with(user("user").password("pass").roles("USER_CUSTOMER")))
                //then
                .andExpect(status().isForbidden())
                .andReturn();

        ResponseData<ShopDto> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("delete stock - no stock with id")
    void test11() throws Exception {

        //given
        Long shopId = 2L;
        Long stockId = 5L;
        var expectedExceptionMessage = "Validations errors: [Stock object:is not present]";

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/stocks/{stockId}", shopId, stockId)
                .with(user("user").password("pass").roles("ADMIN_SHOP")))
                //then
                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseData<ShopDto> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("delete stock - access denied")
    void test12() throws Exception {

        //given
        Long shopId = 2L;
        Long stockId = 5L;
        var expectedExceptionMessage = "Access is denied";

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/stocks/{stockId}", shopId, stockId)
                .with(user("user").password("pass").roles("USER_CUSTOMER")))
                //then
                .andExpect(status().isForbidden())
                .andReturn();

        ResponseData<ShopDto> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });


        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("delete stock - product in stock present - cannot delete a stock")
    @Transactional
    void test13() throws Exception {

        //given
        Shop shop = Shop.builder().name("Media Markt").build();
        Stock stock = Stock.builder().shop(shop).build();
        shop.setStocks(Set.of(stock));
        Product product = Product.builder().name("yr10p").build();
        stock.setProductsQuantity(Map.of(product, 10));

        entityManager.persist(shop);
        entityManager.persist(stock);
        entityManager.persist(product);

        var expectedExceptionMessage = "Validations errors: [Stock products:There are products in store. Move them in another store before removing a stock]";

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/stocks/{stockId}", shop.getId(), stock.getId())
                .with(user("user").password("pass").roles("ADMIN_SHOP")))
                //then
                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseData<ShopDto> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });


        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("delete stock - successful")
    @Transactional
    void test14() throws Exception {

        //given
        Shop shop = Shop.builder().name("Media Markt").build();
        Stock stock = Stock.builder().shop(shop).build();
        shop.setStocks(Set.of(stock));
        stock.setProductsQuantity(Collections.emptyMap());

        entityManager.persist(shop);
        entityManager.persist(stock);


        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/stocks/{stockId}", shop.getId(), stock.getId())
                .with(user("user").password("pass").roles("ADMIN_SHOP")))
                //then
                .andExpect(status().isNoContent());


    }

    @Test
    @DisplayName("add shop - access denied")
    void test15() throws Exception {

        String expectedExceptionMessage = "Access is denied";

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/shops")
                .with(user("user").password("pass").roles("USER_CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
        )
                //then
                .andExpect(status().isForbidden())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });


        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("add shop - validation errors")
    void test16() throws Exception {

        String expectedExceptionMessage = "Validations errors: [Shop budget:Sho budget is not valid, Shop name:Shop name is not valid, Shop address:Shop address is not valid]";

        var body = CreateShopDto.builder().build();
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/shops")
                .with(user("user").password("pass").roles("ADMIN_SHOP"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body))
        )
                //then
                .andExpect(status().isBadRequest())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });


        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("add shop - successful")
    @Transactional
    void test17() throws Exception {

        var body = CreateShopDto.builder()

                .address("Długa 77")
                .budget(new BigDecimal("100000"))
                .name("Samsung shore")
                .stocks(Set.of(
                        CreateStockDto.builder()
                                .address("Długa 77")
                                .build()
                ))
                .build();
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/shops")
                .with(user("user").password("pass").roles("ADMIN_SHOP"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body))
        )
                //then
                .andExpect(status().isCreated())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });


        //then

        assertThat(result.getData(), is(notNullValue()));

    }

    @Test
    @DisplayName("update shop - access denied")
    void test18() throws Exception {


        //given
        String expectedExceptionMessage = "Access is denied";
        Long shopId = 21L;

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/shops/{id}", shopId)
                .with(user("user").password("pass").roles("USER_CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
        )
                //then
                .andExpect(status().isForbidden())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("update shop - no shop with id")
    void test19() throws Exception {


        //given
        Long shopId = 21L;
        String expectedExceptionMessage = "No shop with id: " + shopId;
        UpdateShopDto updateShopDto = UpdateShopDto.builder()
                .name("Samsung new")
                .build();


        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/shops/{id}", shopId)
                .with(user("user").password("pass").roles("ADMIN_SHOP"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateShopDto))
        )
                //then
                .andExpect(status().isNotFound())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("update shop - validation errors")
    @Transactional
    void test20() throws Exception {


        //given
        Shop shop = Shop.builder().name("Samsung").build();
        entityManager.persist(shop);

        String expectedExceptionMessage = "Validations errors: [UpdateShopDto name:is not valid]";

        UpdateShopDto updateShopDto = UpdateShopDto.builder()
                .name("")
                .build();


        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/shops/{id}", shop.getId())
                .with(user("user").password("pass").roles("ADMIN_SHOP"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateShopDto))
        )
                //then
                .andExpect(status().isBadRequest())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("update shop - successful")
    @Transactional
    void test21() throws Exception {


        //given
        Shop shop = Shop.builder().name("Samsung").build();
        entityManager.persist(shop);

        UpdateShopDto updateShopDto = UpdateShopDto.builder()
                .name("samsung new name")
                .build();


        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/shops/{id}", shop.getId())
                .with(user("user").password("pass").roles("ADMIN_SHOP"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateShopDto))
        )
                //then
                .andExpect(status().isOk())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getData(), is(notNullValue()));

    }

    @Test
    @DisplayName("add stock - access denied")
    void test22() throws Exception {

        //given
        Long shopId = 1L;
        var expectedExceptionMessage = "Access is denied";

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/shops/{id}/stocks", shopId)
                .with(user("user").password("pass").roles("USER_CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
        )
                //then
                .andExpect(status().isForbidden())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("add stock - validation errors")
    @Transactional
    void test23() throws Exception {

        //given
        Shop shop = Shop.builder().name("Samsung").build();
        entityManager.persist(shop);
        var expectedExceptionMessage = "Validations errors: [Stock address:cannot be blank]";

        CreateStockDto createStockDto = CreateStockDto.builder().address("").build();
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/shops/{id}/stocks", shop.getId())
                .with(user("user").password("pass").roles("ADMIN_SHOP"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createStockDto))
        )
                //then
                .andExpect(status().isBadRequest())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("add stock - a stock with same address already belongs to specified shop")
    @Transactional
    void test24() throws Exception {

        //given
        Shop shop = Shop.builder().name("Samsung").build();
        Address address = Address.builder().address("Long street").build();
        Stock stock = Stock.builder().address(address).shop(shop).build();

        entityManager.persist(address);
        entityManager.persist(shop);
        entityManager.persist(stock);

        var expectedExceptionMessage = "Validations errors: [Stock address:There is already a stock for this shop under this address]";

        var createStockDto = CreateStockDto.builder().address("Long street").build();
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/shops/{id}/stocks", shop.getId())
                .with(user("user").password("pass").roles("ADMIN_SHOP"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createStockDto))
        )
                //then
                .andExpect(status().isBadRequest())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("add stock - no shop with id")
    @Transactional
    void test25() throws Exception {

        //given

        var expectedExceptionMessage = "Validations errors: [Shop id:No shop with id: 45]";

        var createStockDto = CreateStockDto.builder().address("Long street").build();
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/shops/{id}/stocks", 45L)
                .with(user("user").password("pass").roles("ADMIN_SHOP"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createStockDto))
        )
                //then
                .andExpect(status().isBadRequest())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("add stock - successful")
    @Transactional
    void test26() throws Exception {

        //given
        Shop shop = Shop.builder().name("samsung").build();
        entityManager.persist(shop);
        var createStockDto = CreateStockDto.builder().address("Long street").build();

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/shops/{id}/stocks", shop.getId())
                .with(user("user").password("pass").roles("ADMIN_SHOP"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createStockDto))
        )
                //then
                .andExpect(status().isCreated())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(nullValue()));
        assertThat(result.getData(), is(notNullValue()));
    }

    @Test
    @DisplayName("updated stock - access denied")
    void test27() throws Exception {

        //given

        var expectedExceptionMessage = "Access is denied";

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/shops/{id}/stocks", 45L)
                .with(user("user").password("pass").roles("USER_CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
        )
                //then
                .andExpect(status().isForbidden())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("updated stock - validation errors")
    @Transactional
    void test28() throws Exception {

        //given

        Shop shop = Shop.builder().name("samsung").build();
        Address address = Address.builder().address("Long street").build();
        Stock stock = Stock.builder().address(address).shop(shop).build();
        shop.setStocks(Set.of(stock));
        Map<String, String> properties = Map.of("address", "");

        entityManager.persist(address);
        entityManager.persist(shop);
        entityManager.persist(stock);

        var expectedExceptionMessage = "Address cannot be blank";

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/shops/{shopId}/stocks/{stockId}", shop.getId(), stock.getId())
                .with(user("user").password("pass").roles("ADMIN_SHOP"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(properties))
        )
                //then
                .andExpect(status().isBadRequest())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("updated stock - successful")
    @Transactional
    void test29() throws Exception {

        //given

        Shop shop = Shop.builder().name("samsung").build();
        Address address = Address.builder().address("Long street").build();
        Stock stock = Stock.builder().address(address).shop(shop).build();
        shop.setStocks(Set.of(stock));
        Map<String, String> properties = Map.of("address", "Sobieski street");

        entityManager.persist(address);
        entityManager.persist(shop);
        entityManager.persist(stock);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/shops/{shopId}/stocks/{stockId}", shop.getId(), stock.getId())
                .with(user("user").password("pass").roles("ADMIN_SHOP"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(properties))
        )
                //then
                .andExpect(status().isOk())
                .andReturn();


        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(nullValue()));
        assertThat(result.getData(), is(stock.getId()));
    }
}