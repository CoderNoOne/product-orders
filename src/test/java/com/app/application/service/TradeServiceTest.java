package com.app.application.service;

import com.app.domain.entity.Trade;
import com.app.domain.repository.TradeRepository;
import com.app.infrastructure.dto.TradeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private TradeService tradeService;

    @Test
    @DisplayName("get all")
    void test1() {

        //given
        var expectedResult = List.of(

                TradeDto.builder().name("Electronics").build(),
                TradeDto.builder().name("Automotive").build()
        );

        given(tradeRepository.findAll())
                .willReturn(List.of(
                        Trade.builder().name("Electronics").build(),
                        Trade.builder().name("Automotive").build()
                ));
        //when
        List<TradeDto> actual = Assertions.assertDoesNotThrow(() -> tradeService.getAllTrades());

        //then
        assertThat(actual, is(equalTo(expectedResult)));

        then(tradeRepository).should(times(1)).findAll();
    }
}