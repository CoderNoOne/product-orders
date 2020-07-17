package com.app.infrastructure.controller;

import com.app.ProductOrdersApplication;
import com.app.domain.entity.*;
import com.app.domain.enums.MeetingStatus;
import com.app.domain.enums.ProposalStatus;
import com.app.infrastructure.dto.CreateMeetingDto;
import com.app.infrastructure.dto.CreateNoticeForMeetingDto;
import com.app.infrastructure.dto.NoticeDto;
import com.app.infrastructure.dto.ResponseData;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=2222",
        classes = ProductOrdersApplication.class
)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
class MeetingControllerIntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private Long mockData1() {
        var savedRoleId = entityManager.merge(Role.builder().name("USER_MANAGER").build()).getId();

        var savedManagerId = entityManager.merge(Manager.builder().username("manager").role(entityManager.find(Role.class, savedRoleId)).build()).getId();

        var customerToSave = Customer.builder()
                .manager(entityManager.find(Manager.class, savedManagerId))
                .username("customer")
                .build();

        var savedCustomerId = entityManager.merge(customerToSave).getId();

        var productOrderProposalToSave = ProductOrderProposal
                .builder()
                .id(1L)
                .customer(entityManager.find(Customer.class, savedCustomerId))
                .status(ProposalStatus.PROPOSED)
                .build();


        var savedOrderProposalId = entityManager.merge(productOrderProposalToSave).getId();

        final Meeting meetingToSave = Meeting.builder()
                .orderProposal(entityManager.find(ProductOrderProposal.class, savedOrderProposalId))
                .status(MeetingStatus.FINISHED)
                .build();

        return entityManager.merge(meetingToSave).getId();

    }

    private Long mockData2() {
        var savedRoleId = entityManager.merge(Role.builder().name("USER_MANAGER").build()).getId();

        var savedManagerId = entityManager.merge(Manager.builder().username("manager").role(entityManager.find(Role.class, savedRoleId)).build()).getId();

        var customerToSave = Customer.builder()
                .manager(entityManager.find(Manager.class, savedManagerId))
                .username("customer")
                .build();

        var savedCustomerId = entityManager.merge(customerToSave).getId();

        var productOrderProposalToSave = ProductOrderProposal
                .builder()
                .id(1L)
                .customer(entityManager.find(Customer.class, savedCustomerId))
                .status(ProposalStatus.PROPOSED)
                .build();


        var savedOrderProposalId = entityManager.merge(productOrderProposalToSave).getId();

        final Meeting meetingToSave = Meeting.builder()
                .orderProposal(entityManager.find(ProductOrderProposal.class, savedOrderProposalId))
                .status(MeetingStatus.PROPOSED)
                .notices(new ArrayList<>(List.of(Notice.builder().tittle("Tittle").content("Some Content").build())))
                .build();

        return entityManager.merge(meetingToSave).getId();

    }

    @Test
    @Transactional
    @DisplayName("getAllMeetings - all/status ignored")
    void test1() throws Exception {

        //given
        mockData1();

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
        mockData1();

        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/meetings")
                .with(user("customer").password("pass").roles("USER_CUSTOMER"))
                .param("status", "PROPOSED")
        )
                //then
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<List<Meeting>> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(result.getData(), hasSize(0));

    }

    @Test
    @Transactional
    @DisplayName("getAllMeetings - status specified - FINISHED")
    void test3() throws Exception {

        //given
        mockData1();

        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/meetings")
                .with(user("customer").password("pass").roles("USER_CUSTOMER"))
                .param("status", "FINISHED")
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
    @DisplayName("save by customer - access is denied")
    void test4() throws Exception {


        //given
        var createMeetingDto = CreateMeetingDto.builder()
                .meetingDate(LocalDate.of(2020, 10, 20))
                .productOrderProposalId(1L)
                .build();

        var expectedExceptionMessage = "Access is denied";

        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/meetings")
                .with(user("customer").password("pass").roles("USER_CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createMeetingDto)))
                //then
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(expectedExceptionMessage));


    }

    @Test
    @DisplayName("save by manager - productProposal does not exist")
    void test5() throws Exception {


        //given
        var createMeetingDto = CreateMeetingDto.builder()
                .meetingDate(LocalDate.of(2020, 10, 20))
                .productOrderProposalId(1L)
                .build();


        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/meetings")
                .with(user("manager").password("pass").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createMeetingDto)))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is("Validations errors: [ProductProposal object:does not exist]"));


    }

    @Test
    @DisplayName("save by manager - not owning that productProposal")
    @Transactional
    void test6() throws Exception {


        //given
        var savedRoleId = entityManager.merge(Role.builder().name("USER_MANAGER").build()).getId();

        var savedManagerId = entityManager.merge(Manager.builder().username("manager").role(entityManager.find(Role.class, savedRoleId)).build()).getId();

        var customerToSave = Customer.builder()
                .manager(entityManager.find(Manager.class, savedManagerId))
                .username("customer")
                .build();

        var savedCustomerId = entityManager.merge(customerToSave).getId();

        var productOrderProposalToSave = ProductOrderProposal
                .builder()
                .id(1L)
                .customer(entityManager.find(Customer.class, savedCustomerId))
                .status(ProposalStatus.PROPOSED)
                .build();

        var savedProductOrderProposalId = entityManager.merge(productOrderProposalToSave).getId();

        var createMeetingDto = CreateMeetingDto.builder()
                .meetingDate(LocalDate.of(2020, 10, 20))
                .productOrderProposalId(savedProductOrderProposalId)
                .build();


        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/meetings")
                .with(user("other manager").password("pass").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createMeetingDto)))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(equalTo("You cannot add meeting regarding this productOrder proposal.")));

    }

    @Test
    @DisplayName("delete - no meeting with id")
    void test7() throws Exception {

        //given
        var meetingId = 1;
        var expectedExceptionMessage = "No meeting with id: " + meetingId;

        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/meetings/{id}", meetingId)
                .with(user("manager").password("pass").roles("USER_MANAGER")))
                //then
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(equalTo(expectedExceptionMessage)));


    }

    @Test
    @DisplayName("delete - meeting has FINISHED status")
    @Transactional
    void test8() throws Exception {

        //given
        final Long savedMeetingId = mockData1();
        var expectedExceptionMessage = "Meeting has been finished. Cannot be canceled";

        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/meetings/{id}", savedMeetingId)
                .with(user("manager").password("pass").roles("USER_MANAGER")))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("delete - not owning meeting")
    @Transactional
    void test9() throws Exception {

        //given
        final Long savedMeetingId = mockData2();
        var expectedExceptionMessage = "You have no permission to delete this meeting";

        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/meetings/{id}", savedMeetingId)
                .with(user("other manager").password("pass").roles("USER_MANAGER")))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("delete - successful")
    @Transactional
    void test10() throws Exception {

        //given
        final Long savedMeetingId = mockData2();

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/meetings/{id}", savedMeetingId)
                .with(user("manager").password("pass").roles("USER_MANAGER")))
                //then
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("delete - customer try/access denied")
    @Transactional
    void test11() throws Exception {

        //given
        final Long savedMeetingId = mockData2();

        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/meetings/{id}", savedMeetingId)
                .with(user("customer").password("pass").roles("USER_CUSTOMER")))
                //then
                .andExpect(status().isForbidden())
                .andReturn();

        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is("Access is denied"));
    }

    @Test
    @DisplayName("delete - not logged try")
    @Transactional
    void test12() throws Exception {

        //given
        final Long savedMeetingId = mockData2();

        //when
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/meetings/{id}", savedMeetingId))
                //then
                .andExpect(status().isUnauthorized())
                .andReturn();

        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is("Full authentication is required to access this resource"));
    }

    @Test
    @DisplayName("add notice to meeting - no meeting with id")
    void test13() throws Exception {

        //given
        var createNoticeForMeetingDto = CreateNoticeForMeetingDto.builder().tittle("Tittle").content("Some Content").build();
        var expectedExceptionMessage = "Validations errors: [Meeting id:No meeting with id 1]";

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/meetings/{id}/notices", 1)
                .with(user("manager").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeForMeetingDto))
        )
                //then
                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(expectedExceptionMessage));

    }

    @Test
    @DisplayName("add notice to meeting - try by customer/access denied")
    void test14() throws Exception {

        //given
        var createNoticeForMeetingDto = CreateNoticeForMeetingDto.builder().tittle("Tittle").content("Some Content").build();
        var expectedExceptionMessage = "Access is denied";

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/meetings/{id}/notices", 1)
                .with(user("customer").roles("USER_CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeForMeetingDto))
        )
                //then
                .andExpect(status().isForbidden())
                .andReturn();

        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(expectedExceptionMessage));

    }

    @Test
    @DisplayName("add notice to meeting - not logged")
    void test15() throws Exception {

        //given
        var createNoticeForMeetingDto = CreateNoticeForMeetingDto.builder().tittle("Tittle").content("Some Content").build();
        var expectedExceptionMessage = "Full authentication is required to access this resource";

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/meetings/{id}/notices", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeForMeetingDto))
        )
                //then
                .andExpect(status().isUnauthorized())
                .andReturn();

        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(expectedExceptionMessage));

    }

    @Test
    @DisplayName("add notice to meeting - no permission")
    @Transactional
    void test16() throws Exception {

        //given
        var savedMeetingId = mockData2();
        var createNoticeForMeetingDto = CreateNoticeForMeetingDto.builder().tittle("Tittle").content("Some Content").build();
        var expectedExceptionMessage = "No permission. You are not manager of this meeting";

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/meetings/{id}/notices", savedMeetingId)
                .with(user("other manager").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeForMeetingDto))
        )
                //then
                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(expectedExceptionMessage));

    }

    @Test
    @DisplayName("add notice to meeting - successful")
    @Transactional
    void test17() throws Exception {

        //given
        var savedMeetingId = mockData2();
        var createNoticeForMeetingDto = CreateNoticeForMeetingDto.builder().tittle("Tittle").content("Some Content").build();

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/meetings/{id}/notices", savedMeetingId)
                .with(user("manager").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeForMeetingDto))
        )
                //then
                .andExpect(status().isCreated())
                .andReturn();

        ResponseData<Long> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getData(), is(notNullValue()));

    }

    @Test
    @DisplayName("get notices for meeting - successful for manager")
    @Transactional
    void test18() throws Exception {

        //given
        var savedMeetingId = mockData2();
        var createNoticeForMeetingDto = CreateNoticeForMeetingDto.builder().tittle("Tittle").content("Some Content").build();

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/meetings/{id}/notices", savedMeetingId)
                .with(user("manager").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeForMeetingDto))
        )
                //then
                .andExpect(status().isOk())
                .andReturn();

        ResponseData<List<NoticeDto>> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getData(), hasSize(1));

    }

    @Test
    @DisplayName("get notices for meeting - successful for customer")
    @Transactional
    void test19() throws Exception {

        //given
        var savedMeetingId = mockData2();
        var createNoticeForMeetingDto = CreateNoticeForMeetingDto.builder().tittle("Tittle").content("Some Content").build();

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/meetings/{id}/notices", savedMeetingId)
                .with(user("customer").roles("USER_CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeForMeetingDto))
        )
                //then
                .andExpect(status().isOk())
                .andReturn();

        ResponseData<List<NoticeDto>> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getData(), hasSize(1));

    }

    @Test
    @DisplayName("get notices for meeting - customer - no permission")
    @Transactional
    void test20() throws Exception {

        //given
        var savedMeetingId = mockData2();
        entityManager.persist(Customer.builder().username("other customer").build());

        var createNoticeForMeetingDto = CreateNoticeForMeetingDto.builder().tittle("Tittle").content("Some Content").build();
        var expectedExceptionMessage = "No permission. You are not customer of this meeting";

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/meetings/{id}/notices", savedMeetingId)
                .with(user("other customer").roles("USER_CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeForMeetingDto))
        )
                //then
                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseData<List<NoticeDto>> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("get notices for meeting - manager - no permission")
    @Transactional
    void test21() throws Exception {

        //given
        var savedMeetingId = mockData2();
        entityManager.persist(Manager.builder().username("other manager").build());
        var createNoticeForMeetingDto = CreateNoticeForMeetingDto.builder().tittle("Tittle").content("Some Content").build();
        var expectedExceptionMessage = "No permission. You are not manager of this meeting";

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/meetings/{id}/notices", savedMeetingId)
                .with(user("other manager").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeForMeetingDto))
        )
                //then
                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseData<List<NoticeDto>> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("get notices for meeting - manager - no meeting with id")
    @Transactional
    void test22() throws Exception {

        //given
        var meetingId = 1L;
        entityManager.persist(Manager.builder().username("manager").build());
        var createNoticeForMeetingDto = CreateNoticeForMeetingDto.builder().tittle("Tittle").content("Some Content").build();
        var expectedExceptionMessage = "No meeting with id: 1";

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/meetings/{id}/notices", meetingId)
                .with(user("manager").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeForMeetingDto))
        )
                //then
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();

        ResponseData<List<NoticeDto>> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("get notices for meeting - customer - no meeting with id")
    @Transactional
    void test23() throws Exception {

        //given
        var meetingId = 1L;
        entityManager.persist(Customer.builder().username("customer").build());
        var createNoticeForMeetingDto = CreateNoticeForMeetingDto.builder().tittle("Tittle").content("Some Content").build();
        var expectedExceptionMessage = "No meeting with id: 1";

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/meetings/{id}/notices", meetingId)
                .with(user("customer").roles("USER_CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeForMeetingDto))
        )
                //then
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();

        ResponseData<List<NoticeDto>> response = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getError(), is(equalTo(expectedExceptionMessage)));

    }


}

