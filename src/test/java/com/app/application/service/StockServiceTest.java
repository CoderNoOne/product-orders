package com.app.application.service;

import com.app.application.validators.impl.AddProductToStockDtoValidator;
import com.app.application.validators.impl.ProductQuantityDtoValidator;
import com.app.application.validators.impl.TransferProductDtoValidator;
import com.app.domain.entity.*;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.domain.repository.AdminShopPropertyRepository;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.dto.AddProductToStockDto;
import com.app.infrastructure.dto.TransferProductDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import com.app.infrastructure.dto.createShop.ProductQuantityDto;
import com.app.infrastructure.exception.AdminPropertyNotFoundException;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
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

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

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

    @Test
    @DisplayName("addProductToStock - budget limit exceeded")
    void test4() {

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
                .quantity(10)
                .build();

        var expectedExceptionMessage = "Cannot add products to stock - budget limit";


        given(addProductToStockDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(addProductToStockDtoValidator.hasErrors())
                .willReturn(false);

        given(stockRepository.findOne(stockId)).willReturn(Optional.of(Stock.builder()
                .id(stockId)
                .shop(Shop.builder()
                        .name("Samsung")
                        .budget(new BigDecimal("0"))
                        .build())
                .build()));

        given(productRepository.findByNameAndProducerName(addProductToStockDto.getProductInfo().getName(), addProductToStockDto.getProductInfo().getProducerName()))
                .willReturn(Optional.of(Product.builder()
                        .name(addProductToStockDto.getProductInfo().getName())
                        .producer(Producer.builder().name(addProductToStockDto.getProductInfo().getProducerName()).build())
                        .price(new BigDecimal("3000"))
                        .build()));

        given(adminShopPropertyRepository.findByProperty(AdminShopPropertyName.X))
                .willReturn(Optional.of(AdminShopProperty.builder().property(AdminShopPropertyName.X).value(new BigDecimal("0.2")).build()));


        //when
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> stockService.addProductToStock(addProductToStockDto));

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

    @Test
    @DisplayName("addProductToStock - successful")
    void test5() {

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
                .quantity(10)
                .build();

        given(addProductToStockDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(addProductToStockDtoValidator.hasErrors())
                .willReturn(false);

        given(stockRepository.findOne(stockId)).willReturn(Optional.of(Stock.builder()
                .id(stockId)
                .shop(Shop.builder()
                        .name("Samsung")
                        .budget(new BigDecimal("10000000"))
                        .build())
                .productsQuantity(new HashMap<>())
                .build()));

        given(productRepository.findByNameAndProducerName(addProductToStockDto.getProductInfo().getName(), addProductToStockDto.getProductInfo().getProducerName()))
                .willReturn(Optional.of(Product.builder()
                        .id(1L)
                        .name(addProductToStockDto.getProductInfo().getName())
                        .producer(Producer.builder().name(addProductToStockDto.getProductInfo().getProducerName()).build())
                        .price(new BigDecimal("3000"))
                        .build()));

        given(adminShopPropertyRepository.findByProperty(AdminShopPropertyName.X))
                .willReturn(Optional.of(AdminShopProperty.builder().property(AdminShopPropertyName.X).value(new BigDecimal("0.2")).build()));


        //when
        Long result = Assertions.assertDoesNotThrow(() -> stockService.addProductToStock(addProductToStockDto));

        //then
        inOrder.verify(addProductToStockDtoValidator).validate(any());
        inOrder.verify(addProductToStockDtoValidator).hasErrors();
        inOrder.verify(stockRepository).findOne(stockIdCaptor.capture());
        inOrder.verify(productRepository).findByNameAndProducerName(addProductToStockDto.getProductInfo().getName(), addProductToStockDto.getProductInfo().getProducerName());
        inOrder.verify(adminShopPropertyRepository).findByProperty(AdminShopPropertyName.X);
        inOrder.verifyNoMoreInteractions();

        assertThat(result, is(1L));
        assertThat(stockIdCaptor.getValue(), is(stockId));
    }

    @Test
    @DisplayName("getAllProductsForStock - stock id is null")
    void test6() {

        //given
        Long stockId = null;
        var expectedExceptionMessage = "Stock id is null";

        //when
        NullIdValueException exception = Assertions.assertThrows(NullIdValueException.class, () -> stockService.getAllProductsForStock(stockId));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("getAllProductsForStock - no stock with id")
    void test7() {

        //given
        Long stockId = 1L;
        var expectedExceptionMessage = "No stock with id: " + stockId;

        given(stockRepository.findOne(stockId)).willReturn(Optional.empty());

        //when
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> stockService.getAllProductsForStock(stockId));

        //then
        then(stockRepository).should(times(1)).findOne(stockId);
        then(stockRepository).shouldHaveNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("getAllProductsForStock - successful")
    void test8() {

        //given
        Long stockId = 1L;

        var expectedResult = List.of(ProductQuantityDto.builder()
                .productInfo(ProductInfo.builder()
                        .name("y510p")
                        .producerName("Lenovo")
                        .build())
                .quantity(20)
                .build());

        given(stockRepository.findOne(stockId)).willReturn(Optional.of(
                Stock.builder()
                        .id(stockId)
                        .productsQuantity(Map.of(Product.builder()
                                .name("y510p")
                                .producer(Producer.builder().name("Lenovo").build())
                                .build(), 20))
                        .build()
        ));

        //when
        List<ProductQuantityDto> result = Assertions.assertDoesNotThrow(() -> stockService.getAllProductsForStock(stockId));

        //then
        then(stockRepository).should(times(1)).findOne(stockId);
        then(stockRepository).shouldHaveNoMoreInteractions();

        assertThat(result, is(equalTo(expectedResult)));

    }

    @Test
    @DisplayName("relocate products - validation errors")
    void test9() {

        //given
        given(transferProductDtoValidator.validate(any()))
                .willReturn(Map.of("field", "error"));

        given(transferProductDtoValidator.hasErrors())
                .willReturn(true);
        TransferProductDto transferProductDto = TransferProductDto.builder().build();

        var expectedExceptionMessage = "Validations errors: [field:error]";

        //when
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> stockService.relocateProducts(transferProductDto));

        //then
        InOrder inOrder = inOrder(transferProductDtoValidator);

        inOrder.verify(transferProductDtoValidator).validate(any());
        inOrder.verify(transferProductDtoValidator).hasErrors();
        inOrder.verifyNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
    }


    @Test
    @DisplayName("relocate products - no stock from")
    void test10() {

        //given
        given(transferProductDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(transferProductDtoValidator.hasErrors())
                .willReturn(false);


        TransferProductDto transferProductDto = TransferProductDto.builder()
                .quantity(10)
                .productInfo(ProductInfo.builder()
                        .producerName("Lenovo")
                        .name("y510p")
                        .build())
                .stockFrom(1L)
                .stockTo(2L)
                .build();

        given(stockRepository.findOne(1L)).willReturn(Optional.empty());

        var expectedExceptionMessage = "No stock from";

        //when
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> stockService.relocateProducts(transferProductDto));

        //then
        InOrder inOrder = inOrder(transferProductDtoValidator, stockRepository);

        inOrder.verify(transferProductDtoValidator).validate(any());
        inOrder.verify(transferProductDtoValidator).hasErrors();
        inOrder.verify(stockRepository).findOne(1L);
        inOrder.verifyNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("relocate products - no product")
    void test11() {

        //given
        given(transferProductDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(transferProductDtoValidator.hasErrors())
                .willReturn(false);


        TransferProductDto transferProductDto = TransferProductDto.builder()
                .quantity(10)
                .productInfo(ProductInfo.builder()
                        .producerName("Lenovo")
                        .name("y510p")
                        .build())
                .stockFrom(1L)
                .stockTo(2L)
                .build();

        given(stockRepository.findOne(1L)).willReturn(Optional.of(
                Stock.builder()
                        .id(1L)
                        .build()));

        var expectedExceptionMessage = "No product";

        //when
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> stockService.relocateProducts(transferProductDto));

        //then
        InOrder inOrder = inOrder(transferProductDtoValidator, stockRepository, productRepository);

        inOrder.verify(transferProductDtoValidator).validate(any());
        inOrder.verify(transferProductDtoValidator).hasErrors();
        inOrder.verify(stockRepository).findOne(1L);
        inOrder.verify(productRepository).findByNameAndProducerName("y510p", "Lenovo");
        inOrder.verifyNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("relocate products - no stockTo")
    void test12() {

        //given
        given(transferProductDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(transferProductDtoValidator.hasErrors())
                .willReturn(false);


        TransferProductDto transferProductDto = TransferProductDto.builder()
                .quantity(10)
                .productInfo(ProductInfo.builder()
                        .producerName("Lenovo")
                        .name("y510p")
                        .build())
                .stockFrom(1L)
                .stockTo(2L)
                .build();

        given(productRepository.findByNameAndProducerName("y510p", "Lenovo"))
                .willReturn(Optional.of(
                        Product.builder()
                                .id(1L)
                                .producer(Producer.builder().name("Lenovo").build())
                                .name("y510p")
                                .build()
                ));

        given(stockRepository.findOne(1L)).willReturn(Optional.of(
                Stock.builder()
                        .id(1L)
                        .productsQuantity(new HashMap<>())
                        .build()));

        given(stockRepository.findOne(2L)).willReturn(Optional.empty());

        var expectedExceptionMessage = "No stockTo";

        //when
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> stockService.relocateProducts(transferProductDto));

        //then
        InOrder inOrder = inOrder(transferProductDtoValidator, stockRepository, productRepository);

        inOrder.verify(transferProductDtoValidator).validate(any());
        inOrder.verify(transferProductDtoValidator).hasErrors();
        inOrder.verify(stockRepository).findOne(1L);
        inOrder.verify(productRepository).findByNameAndProducerName("y510p", "Lenovo");
        inOrder.verify(stockRepository).findOne(2L);
        inOrder.verifyNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("relocate products - successful")
    void test13() {

        //given
        given(transferProductDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(transferProductDtoValidator.hasErrors())
                .willReturn(false);


        TransferProductDto transferProductDto = TransferProductDto.builder()
                .quantity(10)
                .productInfo(ProductInfo.builder()
                        .producerName("Lenovo")
                        .name("y510p")
                        .build())
                .stockFrom(1L)
                .stockTo(2L)
                .build();

        given(productRepository.findByNameAndProducerName("y510p", "Lenovo"))
                .willReturn(Optional.of(
                        Product.builder()
                                .id(1L)
                                .producer(Producer.builder().name("Lenovo").build())
                                .name("y510p")
                                .build()
                ));

        given(stockRepository.findOne(1L)).willReturn(Optional.of(
                Stock.builder()
                        .id(1L)
                        .productsQuantity(new HashMap<>())
                        .build()));

        given(stockRepository.findOne(2L)).willReturn(Optional.of(
                Stock.builder()
                        .id(2L)
                        .productsQuantity(new HashMap<>())
                        .build()
        ));

        //when
        Long result = Assertions.assertDoesNotThrow(() -> stockService.relocateProducts(transferProductDto));

        //then
        InOrder inOrder = inOrder(transferProductDtoValidator, stockRepository, productRepository);

        inOrder.verify(transferProductDtoValidator).validate(any());
        inOrder.verify(transferProductDtoValidator).hasErrors();
        inOrder.verify(stockRepository).findOne(1L);
        inOrder.verify(productRepository).findByNameAndProducerName("y510p", "Lenovo");
        inOrder.verify(stockRepository).findOne(2L);
        inOrder.verifyNoMoreInteractions();

        assertThat(result, is(equalTo(1L)));
    }
}