package com.app.infrastructure.controller;

import com.app.ProductOrdersApplication;
import com.app.infrastructure.dto.AdminShopPropertyDto;
import com.app.infrastructure.dto.CreateAdminShopPropertyDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.UpdateAdminShopPropertyDto;
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

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
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
class AdminShopPropertyControllerIntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("getAllProperties")
    @Sql(statements = "insert into admin_shop_properties (property, value) values ('X', 0.2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "delete from admin_shop_properties", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test1() throws Exception {

        final MvcResult result =
                //when
                mockMvc.perform(MockMvcRequestBuilders.get("/adminShopProperties")
                        .with(user("admin").password("pass").roles("ADMIN_SHOP")))
                        //then
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn();

        ResponseData<List<AdminShopPropertyDto>> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getData(), Matchers.iterableWithSize(1));
    }

    @Test
    @DisplayName("getPropertyValueByName - value exists")
    @Sql(statements = "insert into admin_shop_properties (property, value) values ('X', 0.2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "delete from admin_shop_properties", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test2() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/adminShopProperties/X")
                .with(user("admin").password("pass").roles("ADMIN_SHOP")))
                //then
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<BigDecimal> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getData(), is(equalTo(new BigDecimal("0.20"))));
    }

    @Test
    @DisplayName("getPropertyValueByName - property name is not valid")
    void test3() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/adminShopProperties/{propertyName}", "asd")
                .with(user("admin").password("pass").roles("ADMIN_SHOP")))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Void> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getError(), is(equalTo("Not valid property name")));
    }

    @Test
    @DisplayName("getPropertyValueByName - value is not present")
    void test4() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/adminShopProperties/X")
                .with(user("user").password("pass").roles("ADMIN_SHOP")))

                //then
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<BigDecimal> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });


        assertThat(actual.getError(), is(equalTo("No value for property: X found")));
    }

    @Test
    @DisplayName("add property value - not valid user role")
    @Sql(statements = "delete from admin_shop_properties", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test5() throws Exception {

        //given

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/adminShopProperties")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("CUSTOMER_USER")))

                //then
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getError(), is(equalTo("Access is denied")));

    }

    @Test
    @DisplayName("add property value - null request body")
    @Sql(statements = "delete from admin_shop_properties", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test6() throws Exception {

        //given

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/adminShopProperties")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN_SHOP")))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getError(), is(equalTo("Validations errors: [CreateAdminShopPropertyDto object:is null]")));

    }


    @Test
    @DisplayName("add property value - successful")
    @Sql(statements = "delete from admin_shop_properties", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test7() throws Exception {

        //given

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/adminShopProperties")
                .content(mapper.writeValueAsString(CreateAdminShopPropertyDto.builder().property("X").value(new BigDecimal("0.3")).build()))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN_SHOP")))

                //then
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });


        assertThat(actual.getData(), is(notNullValue()));

    }

    @Test
    @DisplayName("add property value - bad requestBody value")
    @Sql(statements = "delete from admin_shop_properties", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test8() throws Exception {

        //given

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/adminShopProperties")
                .content(mapper.writeValueAsString(CreateAdminShopPropertyDto.builder().property("not existent field").value(new BigDecimal("0.3")).build()))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN_SHOP")))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getError(), is(equalTo("Validations errors: [Property:is not supported]")));

    }

    @Test
    @DisplayName("delete property value - successful")
    @Sql(statements = "insert into admin_shop_properties (property, value) values ('X', 0.2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "delete from admin_shop_properties", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test9() throws Exception {

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/adminShopProperties/X")
                .with(user("user").password("pass").roles("ADMIN_SHOP")))

                //then
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("delete property value - access denied")
    @Sql(statements = "insert into admin_shop_properties (property, value) values ('X', 0.2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "delete from admin_shop_properties", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test10() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.delete("/adminShopProperties/X")
                .with(user("user").password("pass").roles("CUSTOMER_USER")))

                //then
                .andExpect(status().isForbidden())
                .andReturn();

        ResponseData<Long> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getError(), is(equalTo("Access is denied")));
    }

    @Test
    @DisplayName("delete property value - not valid property")
    @Sql(statements = "insert into admin_shop_properties (property, value) values ('X', 0.2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "delete from admin_shop_properties", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test11() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.delete("/adminShopProperties/ssd")
                .with(user("user").password("pass").roles("ADMIN_SHOP")))

                //then
                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseData<Long> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        System.out.println(actual);

        assertThat(actual.getError(), is(equalTo("Validations errors: [Property name:is not valid]")));
    }


    @Test
    @DisplayName("update property value - property is not valid")
    void test12() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.patch("/adminShopProperties")
                .content(mapper.writeValueAsString(UpdateAdminShopPropertyDto.builder().propertyName("not existent field").value(new BigDecimal("0.3")).build()))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN_SHOP")))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getError(), is(equalTo("Validations errors: [Property name:is not valid]")));

    }

    @Test
    @DisplayName("update property value - property is null")
    void test13() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.patch("/adminShopProperties")
                .content(mapper.writeValueAsString(UpdateAdminShopPropertyDto.builder().propertyName(null).build()))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN_SHOP")))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getError(), is(equalTo("Validations errors: [Property name:is null, Value:is null]")));

    }

    @Test
    @DisplayName("update property value - value not present")
    void test14() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.patch("/adminShopProperties")
                .content(mapper.writeValueAsString(UpdateAdminShopPropertyDto.builder().propertyName("X").value(new BigDecimal("0.2")).build()))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN_SHOP")))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getError(), is(equalTo("Validations errors: [Property name:is not persisted]")));

    }

    @Test
    @DisplayName("update property value - access denied")
    @Sql(statements = "insert into admin_shop_properties (property, value) values ('X', 0.2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "delete from admin_shop_properties", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test15() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.patch("/adminShopProperties")
                .content(mapper.writeValueAsString(UpdateAdminShopPropertyDto.builder().propertyName("X").value(new BigDecimal("0.2")).build()))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("CUSTOMER_USER")))

                //then
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getError(), is(equalTo("Access is denied")));

    }

    @Test
    @DisplayName("update property value - successful")
    @Transactional
    @Sql(statements = "insert into admin_shop_properties (property, value) values ('X', 0.5)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "delete from admin_shop_properties", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test16() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.patch("/adminShopProperties")
                .content(mapper.writeValueAsString(UpdateAdminShopPropertyDto.builder().propertyName("X").value(new BigDecimal("0.2")).build()))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN_SHOP")))

                //then
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actual.getError(), is(nullValue()));

    }

}
