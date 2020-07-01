package com.app.infrastructure.controller;

import com.app.ProductOrdersApplication;
import com.app.domain.entity.*;
import com.app.domain.enums.MeetingStatus;
import com.app.domain.enums.ProposalStatus;
import com.app.infrastructure.dto.ResponseData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
class MeetingControllerIntegrationTest extends Throwable {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private void mockData() {
        var savedRoleId = entityManager.merge(Role.builder().name("USER_MANAGER").build()).getId();

        var savedManagerId = entityManager.merge(Manager.builder().username("manager").role(entityManager.find(Role.class, savedRoleId)).build()).getId();

        var customerToSave = Customer.builder()
                .manager(entityManager.find(Manager.class, savedManagerId))
                .username("customer")
                .build();

        var savedCustomerId = entityManager.merge(customerToSave).getId();

        var productOrderProposalToSave = ProductOrderProposal
                .builder()
                .customer(entityManager.find(Customer.class, savedCustomerId))
                .status(ProposalStatus.PROPOSED)
                .build();


        var savedOrderProposalId = entityManager.merge(productOrderProposalToSave).getId();

        final Meeting meetingToSave = Meeting.builder()
                .orderProposal(entityManager.find(ProductOrderProposal.class, savedOrderProposalId))
                .status(MeetingStatus.PROPOSED)
                .build();

        entityManager.persist(meetingToSave);

    }

    @Test
    @Transactional
    @DisplayName("getAllMeetings - all/status ignored")
    void test1() throws Exception {

        //given
        mockData();

        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/meetings")
                .with(user("customer").password("pass").roles("USER_CUSTOMER"))
                .param("status", (String) null)
        )
                //then
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<List<Meeting>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(result.getData(), hasSize(1));

    }

    @Test
    @Transactional
    @DisplayName("getAllMeetings - status specified - PROPOSED")
    void test2() throws Exception {

        //given
        mockData();

        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/meetings")
                .with(user("customer").password("pass").roles("USER_CUSTOMER"))
                .param("status",  "PROPOSED")
        )
                //then
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<List<Meeting>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(result.getData(), hasSize(1));

    }
    @Test
    @Transactional
    @DisplayName("getAllMeetings - status specified - FINISHED")
    void test3() throws Exception {

        //given
        mockData();

        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/meetings")
                .with(user("customer").password("pass").roles("USER_CUSTOMER"))
                .param("status",  "FINISHED")
        )
                //then
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<List<Meeting>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(result.getData(), hasSize(0));

    }
}

