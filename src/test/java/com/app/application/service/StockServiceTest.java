package com.app.application.service;

import com.app.application.validators.impl.AddProductToStockDtoValidator;
import com.app.application.validators.impl.ProductQuantityDtoValidator;
import com.app.application.validators.impl.TransferProductDtoValidator;
import com.app.domain.entity.Producer;
import com.app.domain.entity.Product;
import com.app.domain.entity.Stock;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.domain.repository.AdminShopPropertyRepository;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.dto.AddProductToStockDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import com.app.infrastructure.exception.AdminPropertyNotFoundException;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@ExtendWith(SpringExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductQuantityDtoValidator productQuantityDtoValidator;
    @Mock
    private TransferProductDtoValidator transferProductDtoValidator;
    @Mock
    private AddProductToStockDtoValidator addProductToStockDtoValidator;
    @Mock
    private AdminShopPropertyRepository adminShopPropertyRepository;

    @InjectMocks
    private StockService stockService;

    @Test
    @DisplayName("addProductToStock - validation errors")
    void test1() {

        //given
        var expectedExceptionMessage = "Validations errors: [field:error]";
        var addProductToStockDto = AddProductToStockDto.builder().build();
        given(addProductToStockDtoValidator.validate(any()))
                .willReturn(Map.of("field", "error"));

        given(addProductToStockDtoValidator.hasErrors())
                .willReturn(true);

        //when
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> stockService.addProductToStock(addProductToStockDto));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("addProductToStock - no stock with id")
    void test2() {

        //given
        InOrder inOrder = inOrder(addProductToStockDtoValidator, stockRepository);
        ArgumentCaptor<Long> stockIdCaptor = ArgumentCaptor.forClass(Long.class);
        Long stockId = 2L;
        var expectedExceptionMessage = "No stock with id: " + stockId;
        var addProductToStockDto = AddProductToStockDto.builder().stockId(stockId).build();

        given(addProductToStockDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(addProductToStockDtoValidator.hasErrors())
                .willReturn(false);

        given(stockRepository.findOne(stockId)).willReturn(Optional.empty());

        //when
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> stockService.addProductToStock(addProductToStockDto));

        //then
        inOrder.verify(addProductToStockDtoValidator).validate(any());
        inOrder.verify(addProductToStockDtoValidator).hasErrors();
        inOrder.verify(stockRepository).findOne(stockIdCaptor.capture());
        inOrder.verifyNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
        assertThat(stockIdCaptor.getValue(), is(stockId));
    }

    @Test
    @DisplayName("addProductToStock - adminShopPropertyRepository not found")
    void test3() {

        //given
        InOrder inOrder = inOrder(addProductToStockDtoValidator, stockRepository, productRepository, adminShopPropertyRepository);
        ArgumentCaptor<Long> stockIdCaptor = ArgumentCaptor.forClass(Long.class);
        Long stockId = 2L;
        var addProductToStockDto = AddProductToStockDto.builder()
                .stockId(stockId)
                .productInfo(ProductInfo.builder()
                        .name("y510s")
                        .producerName("Lenovo")
                        .build())
                .build();

        var expectedExceptionMessage = "No value for X property found. Cannot add product. Try again later";


        given(addProductToStockDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(addProductToStockDtoValidator.hasErrors())
                .willReturn(false);

        given(stockRepository.findOne(stockId)).willReturn(Optional.of(Stock.builder().id(stockId).build()));

        given(productRepository.findByNameAndProducerName(addProductToStockDto.getProductInfo().getName(), addProductToStockDto.getProductInfo().getProducerName()))
                .willReturn(Optional.of(Product.builder()
                        .name(addProductToStockDto.getProductInfo().getName())
                        .producer(Producer.builder().name(addProductToStockDto.getProductInfo().getProducerName()).build())
                        .build()));

        given(adminShopPropertyRepository.findByProperty(AdminShopPropertyName.X))
                .willReturn(Optional.empty());

        //when
        AdminPropertyNotFoundException exception = Assertions.assertThrows(AdminPropertyNotFoundException.class, () -> stockService.addProductToStock(addProductToStockDto));

        //then
        inOrder.verify(addProductToStockDtoValidator).validate(any());
        inOrder.verify(addProductToStockDtoValidator).hasErrors();
        inOrder.verify(stockRepository).findOne(stockIdCaptor.capture());
        inOrder.verify(productRepository).findByNameAndProducerName(addProductToStockDto.getProductInfo().getName(), addProductToStockDto.getProductInfo().getProducerName());
        inOrder.verify(adminShopPropertyRepository).findByProperty(AdminShopPropertyName.X);
        inOrder.verifyNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
        assertThat(stockIdCaptor.getValue(), is(stockId));
    }


}