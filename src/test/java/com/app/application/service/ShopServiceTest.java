package com.app.application.service;

import com.app.application.validators.impl.AddStockToShopDtoValidator;
import com.app.application.validators.impl.CreateShopDtoValidator;
import com.app.application.validators.impl.UpdateShopDtoValidator;
import com.app.domain.entity.Address;
import com.app.domain.entity.Shop;
import com.app.domain.repository.AddressRepository;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.ShopRepository;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.dto.createShop.CreateShopDto;
import com.app.infrastructure.dto.createShop.CreateStockDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    @DisplayName("addShop successful - new Address entity")
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
}