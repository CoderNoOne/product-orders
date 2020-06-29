package com.app.application.service;

import com.app.domain.entity.Producer;
import com.app.domain.repository.ProducerRepository;
import com.app.infrastructure.dto.ProducerDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class ProducerServiceTest {

    @Mock
    private ProducerRepository producerRepository;

    @InjectMocks
    private ProducerService producerService;


    @Test
    @DisplayName("getAllProducers")
    void test1() {

        //given
        List<ProducerDto> expectedResult = List.of(ProducerDto.builder()
                        .name("Lenovo")
                        .guarantees(Collections.emptyList())
                        .build(),
                ProducerDto.builder()
                        .name("Siemens")
                        .guarantees(Collections.emptyList())
                        .build()
        );

        given(producerRepository.findAll())
                .willReturn(List.of(
                        Producer.builder()
                                .name("Lenovo")
                                .build(),
                        Producer.builder()
                                .name("Siemens")
                                .build()
                ));

        //when
        final List<ProducerDto> result = Assertions.assertDoesNotThrow(() -> producerService.getAllProducers());

        //then
        assertThat(result, hasSize(2));
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test
    @DisplayName("getProducersByTrade")
    void test2() {

        //given
        List<ProducerDto> expectedResult = List.of(ProducerDto.builder()
                        .name("Lenovo")
                        .guarantees(Collections.emptyList())
                        .build(),
                ProducerDto.builder()
                        .name("Siemens")
                        .guarantees(Collections.emptyList())
                        .build()
        );

        given(producerRepository.findAllByTrade("Electronics"))
                .willReturn(List.of(
                        Producer.builder()
                                .name("Lenovo")
                                .build(),
                        Producer.builder()
                                .name("Siemens")
                                .build()
                ));

        //when
        final List<ProducerDto> result = Assertions.assertDoesNotThrow(() -> producerService.getProducersByTrade("Electronics"));

        //then
        assertThat(result, hasSize(2));
        assertThat(result, is(equalTo(expectedResult)));

    }
}
