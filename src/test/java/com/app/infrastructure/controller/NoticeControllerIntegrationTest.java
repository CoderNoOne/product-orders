package com.app.infrastructure.controller;

import com.app.ProductOrdersApplication;
import com.app.application.service.UserService;
import com.app.domain.entity.*;
import com.app.domain.enums.MeetingStatus;
import com.app.domain.enums.ProposalStatus;
import com.app.domain.repository.UserRepository;
import com.app.infrastructure.dto.CreateNoticeDto;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
class NoticeControllerIntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("add Notice - access denied")
    void test1() throws Exception {

        //given
        var expectedExceptionMessage = "Access is denied";
        var createNoticeDto = CreateNoticeDto.builder().meetingId(1L).build();

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/notices")
                .with(user("user").password("pass").roles("USER_CUSTOMER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeDto))
        )
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("add Notice - validation errors")
    void test2() throws Exception {

        //given
        var expectedExceptionMessage = "Validations errors: [Content:is null, Tittle:is null]";
        var createNoticeDto = CreateNoticeDto.builder().meetingId(1L).build();

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/notices")
                .with(user("user").password("pass").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeDto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("add Notice - no meeting with id")
    void test3() throws Exception {

        //given
        var meetingId = 1L;
        var expectedExceptionMessage = "No meeting with id: " + meetingId;

        var createNoticeDto = CreateNoticeDto.builder()
                .meetingId(meetingId)
                .tittle("Tittle")
                .content("some content")
                .build();

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/notices")
                .with(user("user").password("pass").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeDto))
        )
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("add Notice - meeting is not managed by you")
    @Transactional
    void test4() throws Exception {

        //given

        Manager manager = Manager.builder().username("other manager").build();
        Customer customer = Customer.builder().manager(manager).build();
        manager.setCustomers(List.of(customer));

        ProductOrderProposal productOrderProposal = ProductOrderProposal.builder()
                .customer(customer)
                .status(ProposalStatus.PROPOSED)
                .build();

        Meeting meeting = Meeting.builder()
                .notices(new ArrayList<>())
                .orderProposal(productOrderProposal)
                .build();


        entityManager.persist(manager);
        entityManager.persist(customer);
        entityManager.persist(productOrderProposal);
        entityManager.persist(meeting);

        var expectedExceptionMessage = "Meeting with id: " + meeting.getId() + " is not managed by you";

        var createNoticeDto = CreateNoticeDto.builder()
                .meetingId(meeting.getId())
                .tittle("Tittle")
                .content("some content")
                .build();

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/notices")
                .with(user("manager").password("pass").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeDto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ResponseData<Long> result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //then

        assertThat(result.getError(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("add Notice - successful")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    void test5() throws Exception {

        //given

        Manager manager = Manager.builder().email("some_mail@gmail.com").username("manager").build();

        Customer customer = Customer.builder().username("customer").email("customer_mail@gmail.com").manager(manager).build();
        manager.setCustomers(List.of(customer));

        Producer producer = Producer.builder().name("Lenovo").build();

        Product product = Product.builder()
                .name("y510p")
                .producer(producer)
                .price(new BigDecimal("3000"))
                .build();
        producer.setProducts(Set.of(product));

        ProductOrderProposal productOrderProposal = ProductOrderProposal.builder()
                .customer(customer)
                .status(ProposalStatus.PROPOSED)
                .product(product)
                .quantity(10)
                .discount(BigDecimal.ZERO)
                .remarks(Collections.emptyList())
                .build();

        Meeting meeting = Meeting.builder()
                .notices(new ArrayList<>())
                .orderProposal(productOrderProposal)
                .status(MeetingStatus.PROPOSED)
                .meetingDate(LocalDate.now())
                .build();


        entityManager.persist(manager);
        entityManager.persist(customer);
        entityManager.persist(productOrderProposal);
        entityManager.persist(meeting);
        entityManager.persist(producer);
        entityManager.persist(product);
        entityManager.flush();

        var createNoticeDto = CreateNoticeDto.builder()
                .meetingId(meeting.getId())
                .tittle("Tittle")
                .content("some content")
                .build();

        //when
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/notices")
                .with(user(manager.getUsername()).password("pass").roles("USER_MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createNoticeDto))
        )
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