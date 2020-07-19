package com.app.infrastructure.controller;

import com.app.ProductOrdersApplication;
import com.app.domain.entity.Trade;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.TradeDto;
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

import java.util.List;

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
class TradeControllerIntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("get all trades - successful")
    @Transactional
    void test1() throws Exception {

        //given
        Trade electronics = Trade.builder().name("Electronics").build();
        Trade automotive = Trade.builder().name("Automotive").build();

        entityManager.persist(electronics);
        entityManager.persist(automotive);

        var expectedResult = List.of(
                TradeDto.builder()
                        .id(electronics.getId())
                        .name(electronics.getName())
                        .build(),
                TradeDto.builder()
                        .id(automotive.getId())
                        .name(automotive.getName())
                        .build()
        );

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/trades")
                .with(user("user").password("pass").roles("USER_CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<List<TradeDto>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(nullValue()));
        assertThat(result.getData(), is(equalTo(expectedResult)));

    }

    @Test
    @DisplayName("get all trades - access denied")
    void test2() throws Exception {

        var expectedExceptionMessage = "Access is denied";

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/trades")
                .with(user("user").password("pass").roles("USER_MANAGER")))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<List<TradeDto>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then
        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }
}