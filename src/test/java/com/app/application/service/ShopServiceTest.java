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
import com.app.infrastructure.dto.UpdateShopDto;
import com.app.infrastructure.dto.createShop.CreateShopDto;
import com.app.infrastructure.dto.createShop.CreateStockDto;
import com.app.infrastructure.exception.*;
import org.hibernate.annotations.NotFound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("delete - shop Id is null. Excetion should be thrown")
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

    @Test
    @DisplayName("update shop - id is null")
    void test10() {

        //given
        Long id = null;
        var expectedExceptionMessage = "Id is null";

        //when
        NullIdValueException exception = assertThrows(NullIdValueException.class, () -> shopService.updateShop(id, UpdateShopDto.builder().build()));

        //then

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("update shop - updateShopDto is null")
    void test11() {

        //given
        Long id = 1L;
        var expectedExceptionMessage = "UpdateShopDto is null";

        //when
        NullReferenceException exception = assertThrows(NullReferenceException.class, () -> shopService.updateShop(id, null));

        //then

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("update shop - validations errors")
    void test12() {

        //given
        Long id = 1L;

        given(updateShopDtoValidator.validate(any()))
                .willReturn(Map.of("field", "error message"));

        UpdateShopDto updateShopDto = UpdateShopDto.builder()
                .address("Long")
                .name("new name")
                .build();

        given(updateShopDtoValidator.hasErrors()).willReturn(true);

        var expectedExceptionMessage = "Validations errors: [field:error message]";

        //when
        ShopValidationException exception = assertThrows(ShopValidationException.class, () -> shopService.updateShop(id, updateShopDto));

        //then
        InOrder inOrder = inOrder(updateShopDtoValidator, shopRepository);
        inOrder.verify(updateShopDtoValidator).validate(updateShopDto);
        inOrder.verify(updateShopDtoValidator).hasErrors();
        inOrder.verifyNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("update shop - no shop with id")
    void test13() {

        //given
        Long id = 1L;

        given(updateShopDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        UpdateShopDto updateShopDto = UpdateShopDto.builder()
                .address("Long")
                .name("new name")
                .build();

        given(updateShopDtoValidator.hasErrors()).willReturn(false);

        given(shopRepository.findOne(id))
                .willReturn(Optional.empty());

        var expectedExceptionMessage = "No shop with id: " + id;

        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> shopService.updateShop(id, updateShopDto));

        //then
        InOrder inOrder = inOrder(updateShopDtoValidator, shopRepository);
        inOrder.verify(updateShopDtoValidator).validate(updateShopDto);
        inOrder.verify(updateShopDtoValidator).hasErrors();
        inOrder.verify(shopRepository).findOne(id);
        inOrder.verifyNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("update shop - no shop with id")
    void test14() {

        //given
        Long id = 1L;

        given(updateShopDtoValidator.validate(any()))
                .willReturn(Collections.emptyMap());

        UpdateShopDto updateShopDto = UpdateShopDto.builder()
                .address("Long")
                .name("new name")
                .build();

        given(addressRepository.findByAddress("Long"))
                .willReturn(Optional.of(Address.builder()
                        .id(2L)
                        .address("Long")
                        .build()));

        given(updateShopDtoValidator.hasErrors()).willReturn(false);

        given(shopRepository.findOne(id))
                .willReturn(Optional.of(Shop.builder()
                        .id(id)
                        .name("old name")
                        .address(Address.builder().id(3L).address("Short").build())
                        .build()));


        //when
        Long actual = assertDoesNotThrow(() -> shopService.updateShop(id, updateShopDto));

        //then
        InOrder inOrder = inOrder(updateShopDtoValidator, shopRepository, addressRepository);
        inOrder.verify(updateShopDtoValidator).validate(updateShopDto);
        inOrder.verify(updateShopDtoValidator).hasErrors();
        inOrder.verify(shopRepository).findOne(id);
        inOrder.verify(addressRepository).findByAddress("Long");
        inOrder.verifyNoMoreInteractions();

        assertThat(actual, is(equalTo(id)));
    }

    @Test
    @DisplayName("updateStock - shop id is null")
    void test15() {

        //given
        Long shopId = null;
        Long stockId = 1L;
        Map<String, String> params = Map.of(
                "address", "Long"
        );

        var expectedExceptionMessage = "Shop id is null";

        //when
        NullIdValueException exception = assertThrows(NullIdValueException.class, () -> shopService.updateStock(shopId, stockId, params));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("updateStock - stock id is null")
    void test16() {

        //given
        Long shopId = 1l;
        Long stockId = null;
        Map<String, String> params = Map.of(
                "address", "Long"
        );

        var expectedExceptionMessage = "Stock id is null";

        //when
        NullIdValueException exception = assertThrows(NullIdValueException.class, () -> shopService.updateStock(shopId, stockId, params));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("updateStock - params map is null")
    void test17() {

        //given
        Long shopId = 1L;
        Long stockId = 1L;
        Map<String, String> params = null;

        var expectedExceptionMessage = "Params map is null";

        //when
        NullReferenceException exception = assertThrows(NullReferenceException.class, () -> shopService.updateStock(shopId, stockId, params));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("updateStock - no stock found")
    void test18() {

        //given
        Long shopId = 1L;
        Long stockId = 1L;
        Map<String, String> params = Map.of(
                "address", "Long"
        );

        var expectedExceptionMessage = MessageFormat.format("No stock for (stockId: {0}, shopId: {1})", stockId, shopId);

        given(stockRepository.findByIdAndShopId(stockId, shopId)).willReturn(Optional.empty());

        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> shopService.updateStock(shopId, stockId, params));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
        then(stockRepository).should(times(1)).findByIdAndShopId(stockId, shopId);
        then(stockRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    @DisplayName("update stock - stock for specified shop and address already exists - exception should be thrown")
    void test19() {

        //given
        Long shopId = 1L;
        Long stockId = 1L;
        Map<String, String> params = Map.of(
                "address", "Short"
        );

        var expectedExceptionMessage = "There is already a stock for shopId: 1 and address: Short";

        given(stockRepository.findByIdAndShopId(stockId, shopId)).willReturn(
                Optional.of(
                        Stock.builder()
                                .id(stockId)
                                .address(Address.builder().address("Long").build())
                                .shop(Shop.builder()
                                        .id(shopId)
                                        .build())
                                .build()
                ));

        given(stockRepository.findByAddressAndShopId("Short", 1L))
                .willReturn(Optional.of(Stock.builder()
                        .id(2L)
                        .address(Address.builder().address("Short").build())
                        .build()));
        //when
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> shopService.updateStock(shopId, stockId, params));

        //then
        then(stockRepository).should(times(1)).findByIdAndShopId(stockId, shopId);
        then(stockRepository).should(times(1)).findByAddressAndShopId("Short", 1L);
        then(stockRepository).shouldHaveNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("update stock - successful")
    void test20() {

        //given
        Long shopId = 1L;
        Long stockId = 1L;
        Map<String, String> params = Map.of(
                "address", "Short"
        );

        var expectedExceptionMessage = "There is already a stock for shopId: 1 and address: Short";

        given(stockRepository.findByIdAndShopId(stockId, shopId)).willReturn(
                Optional.of(
                        Stock.builder()
                                .id(stockId)
                                .address(Address.builder().address("Long").build())
                                .shop(Shop.builder()
                                        .id(shopId)
                                        .build())
                                .build()
                ));

        given(stockRepository.findByAddressAndShopId("Short", 1L))
                .willReturn(Optional.of(Stock.builder()
                        .id(2L)
                        .address(Address.builder().address("Short").build())
                        .build()));
        //when
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> shopService.updateStock(shopId, stockId, params));

        //then
        then(stockRepository).should(times(1)).findByIdAndShopId(stockId, shopId);
        then(stockRepository).should(times(1)).findByAddressAndShopId("Short", 1L);
        then(stockRepository).shouldHaveNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("get all shops with product in store - id is null")
    void test21() {

        //given
        Long id = null;
        var expectedExceptionMessage = "Id is null";

        //when
        NullIdValueException exception = assertThrows(NullIdValueException.class, () -> shopService.getAllShopsWithProductInStore(id));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
        then(shopRepository).shouldHaveNoInteractions();

    }

    @Test
    @DisplayName("get all shops with product in store - no product with id")
    void test22() {

        //given
        var id = 2L;
        var expectedExceptionMessage = "No product with id: " + id;

        given(productRepository.existsById(id)).willReturn(false);

        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> shopService.getAllShopsWithProductInStore(id));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
        then(productRepository).should(times(1)).existsById(id);
        then(productRepository).shouldHaveNoMoreInteractions();
        then(shopRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("get all shops with product in store - successful")
    void test23() {

        //given
        var id = 2L;

        given(productRepository.existsById(id)).willReturn(true);

        given(shopRepository.findAllShopsWithProductInStore(id)).willReturn(
                Set.of(
                        Shop.builder().id(1L).name("Samsung").build(),
                        Shop.builder().id(2L).name("Lenovo").build()
                )
        );

        var expectedResult = List.of(
                ShopDto.builder().id(1L).name("Samsung").stocks(Collections.emptyList()).build(),
                ShopDto.builder().id(2L).name("Lenovo").stocks(Collections.emptyList()).build()
        );

        //when
        List<ShopDto> actual = assertDoesNotThrow(() -> shopService.getAllShopsWithProductInStore(id));

        //then

        then(productRepository).should(times(1)).existsById(id);
        then(productRepository).shouldHaveNoMoreInteractions();
        then(shopRepository).should(times(1)).findAllShopsWithProductInStore(id);
        then(shopRepository).shouldHaveNoMoreInteractions();

        assertThat(actual, is(equalTo(expectedResult)));

    }

    @Test
    @DisplayName("create Stock for shop - id is null")
    void test24() {

        //given
        Long shopId = null;
        var createStockDto = CreateStockDto.builder().address("Long").build();
        var expectedExceptionMessage = "Shop id is null";

        //when
        NullIdValueException exception = assertThrows(NullIdValueException.class, () -> shopService.createStockForShop(shopId, createStockDto));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("create Stock for shop - createStockDto is null")
    void test25() {


        //given
        Long shopId = 1L;
        CreateStockDto createStockDto = null;
        var expectedExceptionMessage = "createStockDto is null";

        //when
        ValidationException exception = assertThrows(ValidationException.class, () -> shopService.createStockForShop(shopId, createStockDto));

        //then
        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));


    }

    @Test
    @DisplayName("create Stock for shop - validation errors")
    void test26() {


        //given
        Long shopId = 1L;
        CreateStockDto createStockDto = CreateStockDto.builder().address("Long").build();
        var expectedExceptionMessage = "Validations errors: [field:error message]";

        given(addStockToShopDtoValidator.validate(any())).willReturn(Map.of("field", "error message"));

        given(addStockToShopDtoValidator.hasErrors()).willReturn(true);

        //when
        ShopValidationException exception = assertThrows(ShopValidationException.class, () -> shopService.createStockForShop(shopId, createStockDto));

        //then

        InOrder inOrder = inOrder(addStockToShopDtoValidator);
        inOrder.verify(addStockToShopDtoValidator).validate(any());
        inOrder.verify(addStockToShopDtoValidator).hasErrors();
        inOrder.verifyNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));


    }

    @Test
    @DisplayName("create Stock for shop - no shop with id")
    void test27() {

        //given
        Long shopId = 1L;
        CreateStockDto createStockDto = CreateStockDto.builder().address("Long").build();
        var expectedExceptionMessage = "No shop with id " + shopId;

        given(addStockToShopDtoValidator.validate(any())).willReturn(Collections.emptyMap());

        given(addStockToShopDtoValidator.hasErrors()).willReturn(false);

        given(shopRepository.findOne(shopId)).willReturn(Optional.empty());

        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> shopService.createStockForShop(shopId, createStockDto));

        //then

        InOrder inOrder = inOrder(addStockToShopDtoValidator, shopRepository);
        inOrder.verify(addStockToShopDtoValidator).validate(any());
        inOrder.verify(addStockToShopDtoValidator).hasErrors();
        inOrder.verify(shopRepository).findOne(shopId);

        inOrder.verifyNoMoreInteractions();

        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
    }

    @Test
    @DisplayName("create Stock for shop - successful")
    void test28() {

        //given
        Long shopId = 1L;
        CreateStockDto createStockDto = CreateStockDto.builder().address("Long").build();
        var generatedStockId = 1L;

        given(addStockToShopDtoValidator.validate(any())).willReturn(Collections.emptyMap());

        given(addStockToShopDtoValidator.hasErrors()).willReturn(false);

        given(shopRepository.findOne(shopId)).willReturn(Optional.of(
                Shop.builder().id(shopId).name("Samsung").address(Address.builder().address("Short").build()).build()
        ));

        given(addressRepository.findByAddress("Long")).willReturn(Optional.of(Address.builder()
                .id(1L).address("Long").build()));

        given(stockRepository.save(any())).willAnswer((Answer<Stock>) invocationOnMock -> {
            Stock stock = invocationOnMock.getArgument(0);
            stock.setId(generatedStockId);
            return stock;
        });

        var stockArgumentCaptor = ArgumentCaptor.forClass(Stock.class);

        //when
        Long actual = assertDoesNotThrow(() -> shopService.createStockForShop(shopId, createStockDto));

        //then

        InOrder inOrder = inOrder(addStockToShopDtoValidator, shopRepository, addressRepository, stockRepository);
        inOrder.verify(addStockToShopDtoValidator).validate(any());
        inOrder.verify(addStockToShopDtoValidator).hasErrors();
        inOrder.verify(shopRepository).findOne(shopId);
        inOrder.verify(addressRepository).findByAddress("Long");
        inOrder.verify(stockRepository).save(stockArgumentCaptor.capture());
        inOrder.verifyNoMoreInteractions();

        assertThat(actual, is(equalTo(generatedStockId)));

        System.out.println(stockArgumentCaptor.getValue().getId());
        System.out.println(stockArgumentCaptor.getValue().getAddress().getId());
        System.out.println(stockArgumentCaptor.getValue().getAddress().getAddress());
        System.out.println(stockArgumentCaptor.getValue().getShop().getName());
        System.out.println(stockArgumentCaptor.getValue().getShop().getStocks());


        assertThat(stockArgumentCaptor.getValue(), is(equalTo(Stock.builder()
                .id(1L)
                .address(Address.builder()
                        .id(1L)
                        .address("Long")
                        .build())
                .shop(Shop.builder()
                        .id(shopId)
                        .name("Samsung")
                        .build())
                .build())));

    }

}

