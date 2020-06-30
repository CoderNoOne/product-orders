package com.app.infrastructure.controller;

import com.app.ProductOrdersApplication;
import com.app.infrastructure.dto.ProducerDto;
import com.app.infrastructure.dto.ResponseData;
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

import java.util.List;

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
class ProducerControllerIntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(statements = "insert into producers (name) values ('Lenovo'), ('Siemens')")
    @Sql(statements = "delete from producers", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("getAll successful")
    void test1() throws Exception {

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/producers")
                .with(user("user").password("pass").roles("USER_CUSTOMER"))
                .param("trade", (String) null)
        )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<List<ProducerDto>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertAll(
                () -> assertThat(result.getData(), hasSize(2)),
                () -> assertThat(result.getData(), hasItem(Matchers.hasProperty("name", Matchers.is("Lenovo")))),
                () -> assertThat(result.getData(), hasItem(Matchers.hasProperty("name", Matchers.is("Siemens"))))
        );
    }

    @Test
    @Sql(statements = "insert into producers (name) values ('Lenovo'), ('Siemens')")
    @Sql(statements = "delete from producers", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("getAll not authenticated")
    void test2() throws Exception {

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/producers")
                        .param("trade", (String) null)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<List<ProducerDto>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertAll(
                () -> assertThat(result.getData(), is(nullValue())),
                () -> assertThat(result.getError(), is("Full authentication is required to access this resource"))
        );
    }

    @Test
    @Sql(statements = "insert into producers (name) values ('Lenovo'), ('Siemens')")
    @Sql(statements = "delete from producers", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("getAll access denied")
    void test3() throws Exception {

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/producers")
                .with(user("user").password("pass").roles("USER_MANAGER"))
                .param("trade", (String) null)
        )
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<List<ProducerDto>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertAll(
                () -> assertThat(result.getData(), is(nullValue())),
                () -> assertThat(result.getError(), is("Access is denied"))
        );
    }

}
