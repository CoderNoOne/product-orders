package com.app.application.service;

import com.app.application.validators.impl.CreateNoticeDtoValidator;
import com.app.domain.entity.*;
import com.app.domain.repository.MeetingRepository;
import com.app.domain.repository.NoticeRepository;
import com.app.infrastructure.dto.CreateNoticeDto;
import com.app.infrastructure.dto.NoticeDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@ExtendWith(SpringExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private CreateNoticeDtoValidator createNoticeDtoValidator;

    @Mock
    private MeetingRepository meetingRepository;

    @InjectMocks
    private NoticeService noticeService;

    @Test
    @DisplayName("save notice - validation exception")
    void test1() {

        //given
        var expectedExceptionMessage = "Validations errors: [field:error]";
        var createNoticeDto = CreateNoticeDto.builder().tittle("").build();
        var managerUsername = "manager";

        given(createNoticeDtoValidator.validate(createNoticeDto))
                .willReturn(Map.of("field", "error"));

        given(createNoticeDtoValidator.hasErrors()).willReturn(true);
        //when
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> noticeService.save(managerUsername, createNoticeDto));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("save notice - no meeting with id")
    void test2() {

        //given
        var meetingId = 2L;
        var expectedExceptionMessage = "No meeting with id: " + meetingId;
        var createNoticeDto = CreateNoticeDto.builder().tittle("").meetingId(meetingId).build();
        var managerUsername = "manager";

        given(meetingRepository.findOne(meetingId))
                .willReturn(Optional.empty());

        given(createNoticeDtoValidator.validate(createNoticeDto))
                .willReturn(Collections.emptyMap());

        given(createNoticeDtoValidator.hasErrors()).willReturn(false);
        //when
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> noticeService.save(managerUsername, createNoticeDto));

        //then
        InOrder inOrder = inOrder(meetingRepository, createNoticeDtoValidator);
        inOrder.verify(createNoticeDtoValidator).validate(createNoticeDto);
        inOrder.verify(createNoticeDtoValidator).hasErrors();
        inOrder.verify(meetingRepository).findOne(meetingId);
        inOrder.verifyNoMoreInteractions();


        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("save notice - meeting with id is not managed by you")
    void test3() {

        //given
        var meetingId = 2L;
        var expectedExceptionMessage = "Meeting with id: " + meetingId + " is not managed by you";
        var createNoticeDto = CreateNoticeDto.builder().tittle("").meetingId(meetingId).build();
        var managerUsername = "manager";

        Customer customer = Customer.builder()
                .username("customer")
                .manager(Manager.builder()
                        .build())
                .build();
        customer.setManager(Manager.builder().username("other manager").customers(List.of(customer)).build());

        given(meetingRepository.findOne(meetingId))
                .willReturn(Optional.of(
                        Meeting.builder()
                                .id(meetingId)
                                .notices(Collections.emptyList())
                                .orderProposal(ProductOrderProposal
                                        .builder()
                                        .customer(customer)
                                        .build())
                                .build()
                ));

        given(createNoticeDtoValidator.validate(createNoticeDto))
                .willReturn(Collections.emptyMap());

        given(createNoticeDtoValidator.hasErrors()).willReturn(false);
        //when
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> noticeService.save(managerUsername, createNoticeDto));

        //then
        InOrder inOrder = inOrder(meetingRepository, createNoticeDtoValidator);
        inOrder.verify(createNoticeDtoValidator).validate(createNoticeDto);
        inOrder.verify(createNoticeDtoValidator).hasErrors();
        inOrder.verify(meetingRepository).findOne(meetingId);
        inOrder.verifyNoMoreInteractions();


        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("save notice - successful")
    void test4() {

        //given
        var meetingId = 2L;
        var createNoticeDto = CreateNoticeDto.builder().tittle("Tittle").meetingId(meetingId).build();
        var managerUsername = "manager";
        Long generatedIdForNotice = 10L;
        var expectedResult = NoticeDto.builder().id(generatedIdForNotice).tittle(createNoticeDto.getTittle()).build();

        Customer customer = Customer.builder()
                .username("customer")
                .manager(Manager.builder()
                        .build())
                .build();
        customer.setManager(Manager.builder().username("manager").customers(List.of(customer)).build());

        given(meetingRepository.findOne(meetingId))
                .willReturn(Optional.of(
                        Meeting.builder()
                                .id(meetingId)
                                .notices(new ArrayList<>())
                                .orderProposal(ProductOrderProposal
                                        .builder()
                                        .customer(customer)
                                        .build())
                                .build()
                ));

        given(createNoticeDtoValidator.validate(createNoticeDto))
                .willReturn(Collections.emptyMap());

        given(createNoticeDtoValidator.hasErrors()).willReturn(false);

        given(noticeRepository.save(any()))
                .willAnswer((Answer<Notice>) invocationOnMock -> {
                    Notice argument = invocationOnMock.getArgument(0);
                    argument.setId(generatedIdForNotice);
                    return argument;
                });

        //when
        NoticeDto actual = Assertions.assertDoesNotThrow(() -> noticeService.save(managerUsername, createNoticeDto));

        //then
        InOrder inOrder = inOrder(meetingRepository, createNoticeDtoValidator, noticeRepository);
        inOrder.verify(createNoticeDtoValidator).validate(createNoticeDto);
        inOrder.verify(createNoticeDtoValidator).hasErrors();
        inOrder.verify(meetingRepository).findOne(meetingId);
        inOrder.verify(noticeRepository).save(any());
        inOrder.verifyNoMoreInteractions();


        assertThat(actual, is(equalTo(expectedResult)));
    }
}