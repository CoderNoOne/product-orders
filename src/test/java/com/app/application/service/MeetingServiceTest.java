package com.app.application.service;

import com.app.application.validators.impl.CreateMeetingDtoValidator;
import com.app.application.validators.impl.CreateNoticeForMeetingDtoValidator;
import com.app.domain.entity.*;
import com.app.domain.enums.MeetingStatus;
import com.app.domain.repository.MeetingRepository;
import com.app.domain.repository.NoticeRepository;
import com.app.domain.repository.ProductOrderProposalRepository;
import com.app.infrastructure.dto.CreateMeetingDto;
import com.app.infrastructure.dto.CreateNoticeForMeetingDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
import com.app.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
class MeetingServiceTest {

    @InjectMocks
    private MeetingService meetingService;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private ProductOrderProposalRepository productOrderProposalRepository;

    @Mock
    private CreateMeetingDtoValidator createMeetingDtoValidator;

    @Mock
    private CreateNoticeForMeetingDtoValidator createNoticeForMeetingDtoValidator;

    private static List<Meeting> mockMeetings() {
        return List.of(
                Meeting.builder()
                        .meetingDate(LocalDate.of(2020, 8, 10))
                        .notices(Collections.emptyList())
                        .status(MeetingStatus.ACCEPTED)
                        .orderProposal(ProductOrderProposal.builder()
                                .product(Product.builder()
                                        .name("y43234z")
                                        .producer(Producer.builder()
                                                .name("lenovo")
                                                .build())
                                        .build())
                                .customer(Customer.builder()
                                        .manager(Manager.builder()
                                                .username("manager")
                                                .build())
                                        .username("customer")
                                        .build())
                                .build())
                        .build(),

                Meeting.builder()
                        .meetingDate(LocalDate.of(2020, 8, 10))
                        .notices(Collections.emptyList())
                        .status(MeetingStatus.ACCEPTED)
                        .orderProposal(ProductOrderProposal.builder()
                                .product(Product.builder()
                                        .name("y43234z")
                                        .producer(Producer.builder()
                                                .name("lenovo")
                                                .build())
                                        .build())
                                .customer(Customer.builder()
                                        .manager(Manager.builder()
                                                .username("other manager")
                                                .build())
                                        .username("other customer")
                                        .build())
                                .build())
                        .build()
        );
    }

    @Test
    @DisplayName("getMeetings for manager")
    void test1() {

        //given
        List<Meeting> meetings = mockMeetings();
        given(meetingRepository.findAll())
                .willReturn(meetings);

        //when
        var result = Assertions.assertDoesNotThrow(() -> meetingService.getMeetings(null, true, "manager"));

        //then
        then(meetingRepository).should(times(1)).findAll();
        then(meetingRepository).shouldHaveNoMoreInteractions();

        assertThat(result, hasSize(1));
    }

    @Test
    @DisplayName("getMeetings for customer")
    void testXX() {

        //given
        List<Meeting> meetings = mockMeetings();
        given(meetingRepository.findAll())
                .willReturn(meetings);

        //when
        var result = Assertions.assertDoesNotThrow(() -> meetingService.getMeetings(null, false, "customer"));

        //then
        then(meetingRepository).should(times(1)).findAll();
        then(meetingRepository).shouldHaveNoMoreInteractions();

        assertThat(result, hasSize(1));
    }

    @Test
    @DisplayName("save Meeting - validation errors")
    void test2() {

        //given
        var expectedExceptionMessage = "Validations errors: [field1:error]";

        given(createMeetingDtoValidator.hasErrors())
                .willReturn(true);
        given(createMeetingDtoValidator.validate(any()))
                .willReturn(Map.of("field1", "error"));

        //when
        var exception = Assertions.assertThrows(ValidationException.class, () -> meetingService.save(
                "manager", CreateMeetingDto.builder().productOrderProposalId(1L).build()));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

        InOrder inOrder = Mockito.inOrder(createMeetingDtoValidator);
        inOrder.verify(createMeetingDtoValidator).validate(any());
        inOrder.verify(createMeetingDtoValidator).hasErrors();
        inOrder.verifyNoMoreInteractions();
        then(meetingRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("save Meeting - no productOrder proposal")
    void test3() {

        //given
        var idCaptor = ArgumentCaptor.forClass(Long.class);

        given(createMeetingDtoValidator.hasErrors())
                .willReturn(false);
        given(createMeetingDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(productOrderProposalRepository.findOne(anyLong())).willReturn(Optional.empty());

        //when
        var exception = Assertions.assertThrows(NotFoundException.class, () -> meetingService.save(
                "manager", CreateMeetingDto.builder().productOrderProposalId(1L).build()));

        //then

        InOrder inOrder = Mockito.inOrder(createMeetingDtoValidator);
        inOrder.verify(createMeetingDtoValidator).validate(any());
        inOrder.verify(createMeetingDtoValidator).hasErrors();
        inOrder.verifyNoMoreInteractions();
        then(productOrderProposalRepository).should(times(1)).findOne(idCaptor.capture());
        then(productOrderProposalRepository).shouldHaveNoMoreInteractions();
        then(meetingRepository).shouldHaveNoInteractions();

        assertThat(idCaptor.getValue(), is(1L));
        assertThat(exception.getMessage(), is(equalTo("No productOrderProposal with id: " + idCaptor.getValue())));
    }

    @Test
    @DisplayName("save Meeting - cannot add meeting - not valid manager username")
    void test4() {

        //given

        var expectedExceptionMessage = "You cannot add meeting regarding this productOrder proposal.";
        var idCaptor = ArgumentCaptor.forClass(Long.class);

        given(createMeetingDtoValidator.hasErrors())
                .willReturn(false);
        given(createMeetingDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(productOrderProposalRepository.findOne(anyLong())).willReturn(Optional.of(
                ProductOrderProposal.builder()
                        .id(1L)
                        .customer(Customer.builder()
                                .manager(Manager.builder()
                                        .username("other manager")
                                        .build())
                                .build())
                        .quantity(10)
                        .build()
        ));

        //when
        var exception = Assertions.assertThrows(ValidationException.class, () -> meetingService.save(
                "manager", CreateMeetingDto.builder().productOrderProposalId(1L).build()));

        //then

        InOrder inOrder = Mockito.inOrder(createMeetingDtoValidator);
        inOrder.verify(createMeetingDtoValidator).validate(any());
        inOrder.verify(createMeetingDtoValidator).hasErrors();
        inOrder.verifyNoMoreInteractions();
        then(productOrderProposalRepository).should(times(1)).findOne(idCaptor.capture());
        then(productOrderProposalRepository).shouldHaveNoMoreInteractions();
        then(meetingRepository).shouldHaveNoInteractions();

        assertThat(idCaptor.getValue(), is(1L));
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("save Meeting - successful")
    void test5() {

        var idCaptor = ArgumentCaptor.forClass(Long.class);
        var meetingCaptor = ArgumentCaptor.forClass(Meeting.class);

        given(createMeetingDtoValidator.hasErrors())
                .willReturn(false);
        given(createMeetingDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        var orderProposal = ProductOrderProposal.builder()
                .id(1L)
                .customer(Customer.builder()
                        .username("customer")
                        .manager(Manager.builder()
                                .username("manager")
                                .build())
                        .build())
                .quantity(10)
                .build();

        given(productOrderProposalRepository.findOne(anyLong())).willReturn(Optional.of(
                orderProposal
        ));

        given(meetingRepository.save(any()))
                .willAnswer((Answer<Meeting>) invocationOnMock -> {

                    Meeting savedMeeting = invocationOnMock.getArgument(0);
                    savedMeeting.setId(1L);
                    return savedMeeting;
                });


        //when
        final Long savedId = Assertions.assertDoesNotThrow(() -> meetingService.save(
                "manager", CreateMeetingDto.builder().productOrderProposalId(1L).meetingDate(LocalDate.of(2020, 10, 20)).build()));

        //then

        InOrder inOrder = Mockito.inOrder(createMeetingDtoValidator, productOrderProposalRepository, meetingRepository);
        inOrder.verify(createMeetingDtoValidator).validate(any());
        inOrder.verify(createMeetingDtoValidator).hasErrors();
        inOrder.verify(productOrderProposalRepository).findOne(idCaptor.capture());
        inOrder.verify(meetingRepository).save(meetingCaptor.capture());
        inOrder.verifyNoMoreInteractions();


        assertThat(idCaptor.getValue(), is(1L));
        assertThat(savedId, is(notNullValue()));

        assertThat(meetingCaptor.getValue().getProposalProductOrder(), is(equalTo(orderProposal)));
    }

    @Test
    @DisplayName("delete meeting - id is null")
    void test6() {

        //given
        var exceptionMessage = "Id is null";

        //when
        var exception = Assertions.assertThrows(NullIdValueException.class, () -> meetingService.delete(null, "manager"));

        //then
        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));
        then(meetingRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("delete meeting - no meeting with id")
    void test7() {

        //given
        var exceptionMessage = "Id is null";

        //when
        var exception = Assertions.assertThrows(NullIdValueException.class, () -> meetingService.delete(null, "manager"));

        //then
        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));
        then(meetingRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("delete meeting - meeting has been finished")
    void test8() {

        //given
        var idCaptor = ArgumentCaptor.forClass(Long.class);
        var exceptionMessage = "Meeting has been finished. Cannot be canceled";

        given(meetingRepository.findOne(any())).willReturn(Optional.of(Meeting.builder().status(MeetingStatus.FINISHED).build()));

        //when
        var exception = Assertions.assertThrows(ValidationException.class, () -> meetingService.delete(1L, "manager"));

        //then
        then(meetingRepository).should(times(1)).findOne(idCaptor.capture());

        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));
        assertThat(idCaptor.getValue(), is(1L));
    }

    @Test
    @DisplayName("delete meeting - meeting doesn't not belong to manager")
    void test9() {

        //given
        var idCaptor = ArgumentCaptor.forClass(Long.class);
        var exceptionMessage = "You have no permission to delete this meeting";

        given(meetingRepository.findOne(any())).willReturn(Optional.of(Meeting.builder().status(MeetingStatus.ACCEPTED)
                .orderProposal(ProductOrderProposal.builder()
                        .customer(Customer.builder()
                                .username("customer")
                                .manager(Manager.builder()
                                        .username("other manager")
                                        .build())
                                .build())
                        .build())
                .build()));

        //when
        var exception = Assertions.assertThrows(ValidationException.class, () -> meetingService.delete(1L, "manager"));

        //then
        then(meetingRepository).should(times(1)).findOne(idCaptor.capture());

        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));
        assertThat(idCaptor.getValue(), is(1L));
    }

    @Test
    @DisplayName("getAllNotices - id is null")
    void test10() {

        //given
        Long id = null;

        //when
        var exception = Assertions.assertThrows(NullIdValueException.class, () -> meetingService.getAllNotices(id, "username", true));

        //then
        assertThat(exception.getMessage(), is(equalTo("Id is null")));
        then(meetingRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("getAllNotices - id not found")
    void test11() {

        //given
        Long id = 1L;
        var exceptionMessage = "No meeting with id: " + id;
        var idCaptor = ArgumentCaptor.forClass(Long.class);

        given(meetingRepository.findOne(id)).willReturn(Optional.empty());

        //when
        var exception = Assertions.assertThrows(NotFoundException.class, () -> meetingService.getAllNotices(id, "username", true));

        //then
        then(meetingRepository).should(times(1)).findOne(idCaptor.capture());
        then(meetingRepository).shouldHaveNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));
        assertThat(idCaptor.getValue(), is(id));
    }

    @Test
    @DisplayName("getAllNotices - no permission for manager")
    void test12() {

        //given
        Long id = 1L;
        var exceptionMessage = "No permission. You are not manager of this meeting";
        var idCaptor = ArgumentCaptor.forClass(Long.class);

        given(meetingRepository.findOne(id)).willReturn(Optional.of(
                Meeting.builder()
                        .id(id)
                        .orderProposal(ProductOrderProposal.builder()
                                .id(2L)
                                .customer(Customer.builder()
                                        .username("customer")
                                        .manager(Manager.builder()
                                                .username("other manager")
                                                .build())
                                        .build())
                                .build())
                        .build()
        ));

        //when
        var exception = Assertions.assertThrows(ValidationException.class, () -> meetingService.getAllNotices(id, "manager", true));

        //then
        then(meetingRepository).should(times(1)).findOne(idCaptor.capture());
        then(meetingRepository).shouldHaveNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));
        assertThat(idCaptor.getValue(), is(id));

    }

    @Test
    @DisplayName("getAllNotices - no permission for customer")
    void test13() {

        //given
        Long id = 1L;
        var exceptionMessage = "No permission. You are not customer of this meeting";
        var idCaptor = ArgumentCaptor.forClass(Long.class);

        given(meetingRepository.findOne(id)).willReturn(Optional.of(
                Meeting.builder()
                        .id(id)
                        .orderProposal(ProductOrderProposal.builder()
                                .id(2L)
                                .customer(Customer.builder()
                                        .username("other customer")
                                        .manager(Manager.builder()
                                                .username("manager")
                                                .build())
                                        .build())
                                .build())
                        .build()
        ));

        //when
        var exception = Assertions.assertThrows(ValidationException.class, () -> meetingService.getAllNotices(id, "customer", false));

        //then
        then(meetingRepository).should(times(1)).findOne(idCaptor.capture());
        then(meetingRepository).shouldHaveNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));
        assertThat(idCaptor.getValue(), is(id));

    }

    @Test
    @DisplayName("getAllNotices - successful for manager")
    void test14() {

        //given
        Long id = 1L;
        var idCaptor = ArgumentCaptor.forClass(Long.class);

        given(meetingRepository.findOne(id)).willReturn(Optional.of(
                Meeting.builder()
                        .id(id)
                        .orderProposal(ProductOrderProposal.builder()
                                .id(2L)
                                .customer(Customer.builder()
                                        .username("customer")
                                        .manager(Manager.builder()
                                                .username("manager")
                                                .build())
                                        .build())
                                .build())
                        .notices(List.of(
                                Notice.builder()
                                        .tittle("title 1")
                                        .content("content 1")
                                        .build(),
                                Notice.builder()
                                        .tittle("tittle 2")
                                        .content("content 2")
                                        .build(),
                                Notice.builder()
                                        .tittle("tittle 3")
                                        .content("content 3")
                                        .build()
                        ))
                        .build()
        ));

        //when
        var result = Assertions.assertDoesNotThrow(() -> meetingService.getAllNotices(id, "manager", true));

        //then
        then(meetingRepository).should(times(1)).findOne(idCaptor.capture());
        then(meetingRepository).shouldHaveNoMoreInteractions();

        assertThat(idCaptor.getValue(), is(id));
        assertThat(result, hasSize(3));
    }

    @Test
    @DisplayName("getAllNotices - successful for customer")
    void test15() {

        //given
        Long id = 1L;
        var idCaptor = ArgumentCaptor.forClass(Long.class);

        given(meetingRepository.findOne(id)).willReturn(Optional.of(
                Meeting.builder()
                        .id(id)
                        .orderProposal(ProductOrderProposal.builder()
                                .id(2L)
                                .customer(Customer.builder()
                                        .username("customer")
                                        .manager(Manager.builder()
                                                .username("manager")
                                                .build())
                                        .build())
                                .build())
                        .notices(List.of(
                                Notice.builder()
                                        .tittle("title 1")
                                        .content("content 1")
                                        .build(),
                                Notice.builder()
                                        .tittle("tittle 2")
                                        .content("content 2")
                                        .build(),
                                Notice.builder()
                                        .tittle("tittle 3")
                                        .content("content 3")
                                        .build()
                        ))
                        .build()
        ));

        //when
        var result = Assertions.assertDoesNotThrow(() -> meetingService.getAllNotices(id, "customer", false));

        //then
        then(meetingRepository).should(times(1)).findOne(idCaptor.capture());
        then(meetingRepository).shouldHaveNoMoreInteractions();

        assertThat(idCaptor.getValue(), is(id));
        assertThat(result, hasSize(3));
    }

    @Test
    @DisplayName("add notice - meeting id is null")
    void test16() {

        //given
        Long id = null;

        //when
        var exception = Assertions.assertThrows(
                NullIdValueException.class,
                () -> meetingService.addNotice(id, CreateNoticeForMeetingDto.builder().build(), "manager"));

        //then
        then(meetingRepository).shouldHaveNoInteractions();

        assertThat(exception.getMessage(), is(equalTo("Meeting id is null")));
    }

    @Test
    @DisplayName("add notice - validations errors")
    void test17() {

        //given
        Long id = 1L;
        var idCaptor = ArgumentCaptor.forClass(Long.class);
        var expectedExceptionMessage = "Validations errors: [field:error, Meeting id:No meeting with id 1]";


        given(createNoticeForMeetingDtoValidator.validate(any()))
                .willReturn(new HashMap<>(Map.of("field", "error")));

        given(createNoticeForMeetingDtoValidator.hasErrors()).willReturn(true);

        given(meetingRepository.findOne(id)).willReturn(Optional.empty());

        //when
        var exception = Assertions.assertThrows(
                ValidationException.class,
                () -> meetingService.addNotice(id, CreateNoticeForMeetingDto.builder().build(), "manager"));

        //then
        then(meetingRepository).should(times(1)).findOne(idCaptor.capture());
        then(meetingRepository).shouldHaveNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
        assertThat(idCaptor.getValue(), is(id));
    }


    @Test
    @DisplayName("add notice - no permission")
    void test18() {

        //given
        Long id = 1L;
        var idCaptor = ArgumentCaptor.forClass(Long.class);
        var expectedExceptionMessage = "No permission. You are not manager of this meeting";


        given(createNoticeForMeetingDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(createNoticeForMeetingDtoValidator.hasErrors()).willReturn(false);

        given(meetingRepository.findOne(id)).willReturn(
                Optional.of(Meeting.builder()
                        .id(id)
                        .orderProposal(ProductOrderProposal.builder()
                                .customer(Customer.builder()
                                        .manager(Manager.builder()
                                                .username("other manager")
                                                .build())
                                        .build())
                                .build())
                        .build()));

        //when
        var exception = Assertions.assertThrows(
                ValidationException.class,
                () -> meetingService.addNotice(id, CreateNoticeForMeetingDto.builder().build(), "manager"));

        //then
        then(meetingRepository).should(times(1)).findOne(idCaptor.capture());
        then(meetingRepository).shouldHaveNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
        assertThat(idCaptor.getValue(), is(id));
    }

    @Test
    @DisplayName("add notice - successful")
    void test19() {

        //given
        Long id = 1L;
        var idCaptor = ArgumentCaptor.forClass(Long.class);


        given(createNoticeForMeetingDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(createNoticeForMeetingDtoValidator.hasErrors()).willReturn(false);

        given(meetingRepository.findOne(id)).willReturn(
                Optional.of(Meeting.builder()
                        .id(id)
                        .orderProposal(ProductOrderProposal.builder()
                                .customer(Customer.builder()
                                        .manager(Manager.builder()
                                                .username("manager")
                                                .build())
                                        .build())
                                .build())
                        .notices(new ArrayList<>())
                        .build()));

        given(noticeRepository.save(any())).willReturn(Notice.builder().id(1L).build());

        //when
        var result = Assertions.assertDoesNotThrow(
                () -> meetingService.addNotice(id, CreateNoticeForMeetingDto.builder().build(), "manager"));

        //then
        InOrder inOrder = Mockito.inOrder(meetingRepository, noticeRepository);
        inOrder.verify(meetingRepository).findOne(idCaptor.capture());
        inOrder.verify(noticeRepository).save(any());
        inOrder.verifyNoMoreInteractions();

        assertThat(result, is(1L));
        assertThat(idCaptor.getValue(), is(id));
    }
}
