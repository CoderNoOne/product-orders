package com.app.application.service;

import com.app.application.validators.impl.AddStockToShopDtoValidator;
import com.app.application.validators.impl.CreateShopDtoValidator;
import com.app.application.validators.impl.UpdateShopDtoValidator;
import com.app.domain.entity.Address;
import com.app.domain.entity.Shop;
import com.app.domain.entity.Stock;
import com.app.domain.repository.AddressRepository;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.ShopRepository;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.dto.ShopDto;
import com.app.infrastructure.dto.createShop.CreateShopDto;
import com.app.infrastructure.dto.createShop.CreateStockDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
import com.app.infrastructure.exception.ShopValidationException;
import org.apache.commons.validator.Arg;
import org.hamcrest.number.BigDecimalCloseTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.parsing.ConstructorArgumentEntry;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
class ShopServiceTest {

    @Mock
    private ShopRepository shopRepository;
    @Mock
    private CreateShopDtoValidator createShopDtoValidator;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private UpdateShopDtoValidator updateShopDtoValidator;
    @Mock
    private AddStockToShopDtoValidator addStockToShopDtoValidator;

    @InjectMocks
    private ShopService shopService;

    @Test
    @DisplayName("addShop validation errors. Exception should be thrown")
    void test1() {

        //given
        CreateShopDto createShopDto = CreateShopDto.builder()
                .name("Samsung")
                .address("asdes")
                .budget(new BigDecimal("50000"))
                .build();

        var exceptionMessage = "Validations errors: [field:error message]";

        var createShopDtoCaptor = ArgumentCaptor.forClass(CreateShopDto.class);

        given(createShopDtoValidator.validate(any()))
                .willReturn(Map.of("field", "error message"));

        given(createShopDtoValidator.hasErrors()).willReturn(true);

        //when
        ShopValidationException exception = assertThrows(ShopValidationException.class, () -> shopService.addShop(createShopDto));

        //then
        InOrder inOrder = inOrder(createShopDtoValidator);
        inOrder.verify(createShopDtoValidator).validate(createShopDtoCaptor.capture());
        inOrder.verify(createShopDtoValidator).hasErrors();
        inOrder.verifyNoMoreInteractions();

        assertThat(exceptionMessage, is(equalTo(exception.getMessage())));
        assertThat(createShopDtoCaptor.getValue(), is(equalTo(createShopDto)));

    }

    @Test
    @DisplayName("addShop successful")
    void test2() {

        //given

        var createShopDtoCaptor = ArgumentCaptor.forClass(CreateShopDto.class);
        var addressValueCaptor = ArgumentCaptor.forClass(String.class);
        var addressObjectCaptor = ArgumentCaptor.forClass(Address.class);
        var shopCaptor = ArgumentCaptor.forClass(Shop.class);

        given(createShopDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        given(createShopDtoValidator.hasErrors())
                .willReturn(false);

        var generatedIdForNewAddress = new AtomicLong(1L);
        var generatedIdForShop = 2L;

        var createShopDto = CreateShopDto.builder()
                .name("Samsung")
                .budget(new BigDecimal("50000"))
                .address("Long")
                .stocks(Set.of(
                        CreateStockDto.builder()
                                .address("Short")
                                .build()
                ))
                .build();

        given(addressRepository.findByAddress(anyString()))
                .willReturn(Optional.empty());

        given(addressRepository.save(any()))
                .willAnswer((Answer<Address>) invocationOnMock -> {
                    var address = (Address) invocationOnMock.getArgument(0);

                    address.setId(generatedIdForNewAddress.getAndIncrement());
                    return address;
                });

        given(shopRepository.save(any()))
                .willAnswer((Answer<Shop>) invocationOnMock -> {
                    Shop shop = invocationOnMock.getArgument(0);
                    shop.setId(generatedIdForShop);
                    return shop;
                });

        //when
        Long result = assertDoesNotThrow(() -> shopService.addShop(createShopDto));

        //then

        InOrder inOrder = inOrder(createShopDtoValidator, addressRepository, shopRepository);

        inOrder.verify(createShopDtoValidator).validate(createShopDtoCaptor.capture());
        inOrder.verify(createShopDtoValidator).hasErrors();
        inOrder.verify(addressRepository).findByAddress(addressValueCaptor.capture());
        inOrder.verify(addressRepository).save(addressObjectCaptor.capture());
        inOrder.verify(addressRepository).findByAddress(addressValueCaptor.capture());
        inOrder.verify(addressRepository).save(addressObjectCaptor.capture());
        inOrder.verify(shopRepository).save(shopCaptor.capture());

        inOrder.verifyNoMoreInteractions();

        assertThat(createShopDtoCaptor.getValue(), is(equalTo(createShopDto)));
        assertThat(addressValueCaptor.getAllValues(), is(equalTo(List.of("Long", "Short"))));

        assertThat(addressObjectCaptor.getAllValues(), hasItems(
                hasProperty("address", is("Long")),
                hasProperty("address", is("Short")))
        );

        assertThat(result, is(equalTo(generatedIdForShop)));
        assertThat(shopCaptor.getValue(), hasProperty("name", is("Samsung")));
        assertThat(shopCaptor.getValue(), hasProperty("address", is(Address.builder().address("Long").id(1L).build())));
        assertThat(shopCaptor.getValue(), hasProperty("budget", is(new BigDecimal("50000"))));
        assertThat(shopCaptor.getValue(), hasProperty("stocks", hasSize(1)));

        assertThat(result, is(generatedIdForShop));
    }

    @Test
    @DisplayName("delete -shop Id is null. Excetion should be thrown")
    void test3() {

        //given
        Long id = null;

        //when
        NullIdValueException exception = assertThrows(NullIdValueException.class, () -> shopService.delete(id));

        //then
        assertThat(exception.getMessage(), is(equalTo("Shop id is null")));

    }

    @Test
    @DisplayName("delete - no shop with id")
    void test4() {

        //given
        Long id = 55L;

        given(shopRepository.findOne(anyLong())).willReturn(Optional.empty());
        var shopIdCaptor = ArgumentCaptor.forClass(Long.class);

        //when

        NotFoundException exception = assertThrows(NotFoundException.class, () -> shopService.delete(id));

        //then
        assertThat(exception.getMessage(), is(equalTo("No shop with id: " + id)));

        then(shopRepository).should(times(1)).findOne(shopIdCaptor.capture());
        then(shopRepository).shouldHaveNoMoreInteractions();

        assertThat(shopIdCaptor.getValue(), is(id));
    }

    @Test
    @DisplayName("delete - successful")
    void test5() {

        //given

        var id = 1L;

        Shop shopToDelete = Shop
                .builder()
                .id(id)
                .name("Samsung")
                .budget(new BigDecimal("50000"))
                .address(Address.builder().id(1L).address("Long").build())
                .stocks(Set.of(
                        Stock.builder()
                                .address(Address.builder()
                                        .id(2L)
                                        .address("Short")
                                        .build())
                                .build()))
                .build();

        given(shopRepository.findOne(id))
                .willReturn(Optional.of(shopToDelete));

        //when
        Assertions.assertDoesNotThrow(() -> shopService.delete(id));


        //then
        InOrder inOrder = inOrder(shopRepository, stockRepository);
        inOrder.verify(shopRepository).findOne(id);
        inOrder.verify(stockRepository).deleteAll(shopToDelete.getStocks());
        inOrder.verify(shopRepository).deleteById(shopToDelete.getId());
        inOrder.verifyNoMoreInteractions();

    }

    @Test
    @DisplayName("get all shops")
    void test6() {

        //given
        List<Shop> shops = List.of(
                Shop.builder()
                        .id(1L)
                        .name("Lenovo")
                        .build(),
                Shop.builder()
                        .id(2L)
                        .name("Samsung")
                        .build()
        );

        var expectedResult = List.of(
                ShopDto.builder()
                        .id(1L)
                        .name("Lenovo")
                        .stocks(Collections.emptyList())
                        .build(),
                ShopDto.builder()
                        .id(2L)
                        .name("Samsung")
                        .stocks(Collections.emptyList())
                        .build()
        );

        given(shopRepository.findAll())
                .willReturn(shops);

        //when
        List<ShopDto> actual = assertDoesNotThrow(() -> shopService.getAllShops());

        //then
        then(shopRepository).should(times(1)).findAll();
        then(shopRepository).shouldHaveNoMoreInteractions();

        assertThat(actual, is(equalTo(expectedResult)));

    }

    @Test
    @DisplayName("get Shop by Id - id is null")
    void test7() {

        //given
        Long id = null;
        var exceptionMessage = "Id is null";

        //when
        NullIdValueException exception = assertThrows(NullIdValueException.class, () -> shopService.getShopById(id));

        //then
        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));

    }

    @Test
    @DisplayName("get shop by id - shop doesn't exist")
    void test8() {

        //given
        Long id = 2L;
        var exceptionMessage = "No shop with id: " + id;

        given(shopRepository.findOne(id))
                .willReturn(Optional.empty());

        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> shopService.getShopById(id));

        //then
        then(shopRepository).should(times(1)).findOne(id);
        then(shopRepository).shouldHaveNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));
    }

    @Test
    @DisplayName("get shop by id - successful")
    void test9() {

        //given
        Long id = 2L;

        given(shopRepository.findOne(id))
                .willReturn(Optional.of(Shop.builder()
                        .id(id)
                        .name("Samsung")
                        .build()));

        var expectedResult = ShopDto.builder()
                .id(id)
                .name("Samsung")
                .stocks(Collections.emptyList())
                .build();

        //when
        ShopDto actual = assertDoesNotThrow(() -> shopService.getShopById(id));

        //then
        then(shopRepository).should(times(1)).findOne(id);
        then(shopRepository).shouldHaveNoMoreInteractions();

        assertThat(actual, is(equalTo(expectedResult)));

    }
}