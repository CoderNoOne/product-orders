package com.app.application.service;

import com.app.application.validators.impl.CreateAdminShopPropertyDtoValidator;
import com.app.application.validators.impl.UpdateAdminShopPropertyDtoValidator;
import com.app.domain.entity.AdminShopProperty;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.domain.repository.AdminShopPropertyRepository;
import com.app.infrastructure.dto.AdminShopPropertyDto;
import com.app.infrastructure.dto.CreateAdminShopPropertyDto;
import com.app.infrastructure.dto.UpdateAdminShopPropertyDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class AdminShopPropertyServiceTest {

    @Mock
    private CreateAdminShopPropertyDtoValidator createAdminShopPropertyDtoValidator;

    @Mock
    private AdminShopPropertyRepository adminShopPropertyRepository;

    @Mock
    private UpdateAdminShopPropertyDtoValidator updateAdminShopPropertyDtoValidator;

    @InjectMocks
    private AdminShopPropertyService adminShopPropertyService;

    @Test
    @DisplayName("getPropertyValueByName - property exists")
    void test1() {

        //given
        BigDecimal defaultValue = new BigDecimal("0.2");

        var propertyCaptor = ArgumentCaptor.forClass(AdminShopPropertyName.class);

        given(adminShopPropertyRepository.findByProperty(any(AdminShopPropertyName.class)))
                .willAnswer(invocationOnMock -> Optional.of(AdminShopProperty.builder().property(invocationOnMock.getArgument(0))
                        .value(defaultValue).build()));

        //when
        var actual = Assertions.assertDoesNotThrow(() -> adminShopPropertyService.getPropertyValueByName(AdminShopPropertyName.Y.name()));

        //then
        verify(adminShopPropertyRepository).findByProperty(propertyCaptor.capture());
        assertThat(actual, is(equalTo(defaultValue)));

    }

    @Test
    @DisplayName("getPropertyValueByName - property name is null")
    void test2() {

        //given
        var expectedExceptionMessage = "Property name is null";

        //when
        final ValidationException actual
                = Assertions.assertThrows(ValidationException.class, () -> adminShopPropertyService.getPropertyValueByName(null));

        //then
        then(adminShopPropertyRepository).shouldHaveNoInteractions();
        assertThat(actual.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("getPropertyValueByName - not valid property name")
    void test3() {

        //given
        var expectedExceptionMessage = "Not valid property name";

        //when
        ValidationException actual
                = Assertions.assertThrows(ValidationException.class, () -> adminShopPropertyService.getPropertyValueByName("asd"));

        //then
        then(adminShopPropertyRepository).shouldHaveNoInteractions();
        assertThat(actual.getMessage(), is(equalTo(expectedExceptionMessage)));

    }

    @Test
    @DisplayName("getPropertyValueByName - no value present")
    void test4() {

        //given

        var propertyCaptor = ArgumentCaptor.forClass(AdminShopPropertyName.class);

        given(adminShopPropertyRepository.findByProperty(any(AdminShopPropertyName.class)))
                .willReturn(Optional.empty());

        //when
        Exception actual
                = Assertions.assertThrows(NotFoundException.class, () -> adminShopPropertyService.getPropertyValueByName(AdminShopPropertyName.X.name()));

        //then
        verify(adminShopPropertyRepository).findByProperty(propertyCaptor.capture());
        assertThat(actual.getMessage(), is(equalTo("No value for property: " + propertyCaptor.getValue() + " found")));

    }

    @Test
    @DisplayName("getAllProperties")
    void test5() {


        //given
        List<AdminShopProperty> propertiesValues = List.of(
                AdminShopProperty.builder()
                        .value(new BigDecimal("0.3"))
                        .property(AdminShopPropertyName.X)
                        .build()
        );

        List<AdminShopPropertyDto> expected = List.of(
                AdminShopPropertyDto.builder()
                        .value(new BigDecimal("0.3"))
                        .name("X")
                        .build()
        );

        given(adminShopPropertyRepository.findAll())
                .willReturn(propertiesValues);

        //when
        List<AdminShopPropertyDto> actual = Assertions.assertDoesNotThrow(() -> adminShopPropertyService.getAllProperties());

        //then
        then(adminShopPropertyRepository).should(times(1)).findAll();
        then(adminShopPropertyRepository).shouldHaveNoMoreInteractions();
        assertThat(actual, is(equalTo(expected)));

    }

    @Test
    @DisplayName("delete property - property is null")
    void test6() {

        //given
        var exceptionMessage = "Validations errors: [Property name:is null]";

        //when
        final ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> adminShopPropertyService.deletePropertyValue(null));

        //then
        then(adminShopPropertyRepository).shouldHaveNoInteractions();
        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));

    }

    @Test
    @DisplayName("delete property - property is not valid")
    void test7() {

        //given
        var exceptionMessage = "Validations errors: [Property name:is not valid]";

        //when
        final ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> adminShopPropertyService.deletePropertyValue("asd"));

        //then
        then(adminShopPropertyRepository).shouldHaveNoInteractions();
        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));

    }

    @Test
    @DisplayName("delete property - no value for property found")
    void test8() {

        //given
        var exceptionMessage = "Validations errors: [Property name:is not valid]";

        //when
        final ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> adminShopPropertyService.deletePropertyValue("asd"));

        //then
        then(adminShopPropertyRepository).shouldHaveNoInteractions();
        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));

    }

    @Test
    @DisplayName("delete property - successful")
    void test9() {

        //given
        BigDecimal defaultValue = new BigDecimal("0.2");

        var propertyCaptor = ArgumentCaptor.forClass(AdminShopPropertyName.class);

        given(adminShopPropertyRepository.findByProperty(any(AdminShopPropertyName.class)))
                .willAnswer(invocationOnMock -> Optional.of(AdminShopProperty.builder().property(invocationOnMock.getArgument(0))
                        .value(defaultValue).build()));

        Mockito.doNothing().when(adminShopPropertyRepository).delete(any(AdminShopProperty.class));

        //when
        Assertions.assertDoesNotThrow(() -> adminShopPropertyService.deletePropertyValue(AdminShopPropertyName.Y.name()));

        //then
        verify(adminShopPropertyRepository).findByProperty(propertyCaptor.capture());
        then(adminShopPropertyRepository).should(times(1))
                .delete(AdminShopProperty.builder().property(propertyCaptor.getValue()).value(defaultValue).build());
        then(adminShopPropertyRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    @DisplayName("addPropertyValue - not valid argument")
    void test10() {

        //given
        var exceptionMessage = "Validations errors: [field:error message]";

        var captor = ArgumentCaptor.forClass(CreateAdminShopPropertyDto.class);

        given(createAdminShopPropertyDtoValidator.validate(any(CreateAdminShopPropertyDto.class)))
                .willReturn(Map.of("field", "error message"));

        given(createAdminShopPropertyDtoValidator.hasErrors())
                .willReturn(true);


        //when
        Exception exception = Assertions
                .assertThrows(ValidationException.class, () -> adminShopPropertyService.addProperty(CreateAdminShopPropertyDto.builder().build()));

        //then
        then(createAdminShopPropertyDtoValidator).should(times(1)).validate(captor.capture());
        then(createAdminShopPropertyDtoValidator).should(times(1)).hasErrors();
        then(createAdminShopPropertyDtoValidator).shouldHaveNoMoreInteractions();
        then(adminShopPropertyRepository).shouldHaveNoInteractions();

        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));
        assertThat(captor.getValue(), is(equalTo(CreateAdminShopPropertyDto.builder().build())));
    }

    @Test
    @DisplayName("addPropertyValue - valid argument")
    void test11() {

        //given
        var createAdminShopPropertyDtoToSave = CreateAdminShopPropertyDto.builder().property("X").value(new BigDecimal("0.3")).build();

        var captor = ArgumentCaptor.forClass(AdminShopPropertyName.class);
        var captor2 = ArgumentCaptor.forClass(CreateAdminShopPropertyDto.class);
        var captor3 = ArgumentCaptor.forClass(AdminShopProperty.class);

        given(createAdminShopPropertyDtoValidator.validate(any(CreateAdminShopPropertyDto.class)))
                .willReturn(Collections.emptyMap());

        given(createAdminShopPropertyDtoValidator.hasErrors())
                .willReturn(false);

        given(adminShopPropertyRepository.findByProperty(any(AdminShopPropertyName.class)))
                .willReturn(Optional.empty());

        given(adminShopPropertyRepository.save(any(AdminShopProperty.class)))
                .willAnswer((Answer<AdminShopProperty>) invocationOnMock -> {

                    AdminShopProperty argument = invocationOnMock.getArgument(0);
                    return AdminShopProperty.builder()
                            .id(1L)
                            .property(argument.getProperty())
                            .value(argument.getValue())
                            .build();
                });
        //when
        Assertions
                .assertDoesNotThrow(() ->
                        adminShopPropertyService.addProperty(createAdminShopPropertyDtoToSave));

        //then
        then(createAdminShopPropertyDtoValidator).should(times(1)).validate(captor2.capture());
        then(createAdminShopPropertyDtoValidator).should(times(1)).hasErrors();
        then(createAdminShopPropertyDtoValidator).shouldHaveNoMoreInteractions();


        then(adminShopPropertyRepository).should(times(1)).findByProperty(captor.capture());
        then(adminShopPropertyRepository).should(times(1)).save(captor3.capture());
        then(adminShopPropertyRepository).shouldHaveNoMoreInteractions();

        assertThat(captor.getValue().toString(), is(equalTo(createAdminShopPropertyDtoToSave.getProperty())));
        assertThat(captor2.getValue(), is(equalTo(createAdminShopPropertyDtoToSave)));
        assertThat(captor3.getValue(), is(equalTo(AdminShopProperty.builder()
                .property(AdminShopPropertyName.valueOf(createAdminShopPropertyDtoToSave.getProperty()))
                .value(createAdminShopPropertyDtoToSave.getValue())
                .build())));
    }

    @Test
    @DisplayName("update - not valid argument")
    void test12() {

        //given
        var exceptionMessage = "Validations errors: [field:error message]";

        var captor = ArgumentCaptor.forClass(UpdateAdminShopPropertyDto.class);

        given(updateAdminShopPropertyDtoValidator.validate(any(UpdateAdminShopPropertyDto.class)))
                .willReturn(Map.of("field", "error message"));

        given(updateAdminShopPropertyDtoValidator.hasErrors())
                .willReturn(true);


        //when
        Exception exception = Assertions
                .assertThrows(ValidationException.class, () -> adminShopPropertyService.updatePropertyValue(UpdateAdminShopPropertyDto.builder().build()));

        //then
        then(updateAdminShopPropertyDtoValidator).should(times(1)).validate(captor.capture());
        then(updateAdminShopPropertyDtoValidator).should(times(1)).hasErrors();
        then(updateAdminShopPropertyDtoValidator).shouldHaveNoMoreInteractions();
        then(adminShopPropertyRepository).shouldHaveNoInteractions();

        assertThat(exception.getMessage(), is(equalTo(exceptionMessage)));
        assertThat(captor.getValue(), is(equalTo(UpdateAdminShopPropertyDto.builder().build())));

    }

    @Test
    @DisplayName("update - valid argument - NotFoundException")
    void test13() {


        //given
        var updateAdminShopPropertyDto = UpdateAdminShopPropertyDto.builder().propertyName("X").value(new BigDecimal("0.3")).build();

        var captor = ArgumentCaptor.forClass(AdminShopPropertyName.class);
        var captor2 = ArgumentCaptor.forClass(UpdateAdminShopPropertyDto.class);

        var expectedExceptionMessage = "Property: X not found";

        given(updateAdminShopPropertyDtoValidator.validate(any(UpdateAdminShopPropertyDto.class)))
                .willReturn(Collections.emptyMap());

        given(updateAdminShopPropertyDtoValidator.hasErrors())
                .willReturn(false);

        given(adminShopPropertyRepository.findByProperty(any(AdminShopPropertyName.class)))
                .willReturn(Optional.empty());

        //when
        NotFoundException exception = Assertions
                .assertThrows(NotFoundException.class, () ->
                        adminShopPropertyService.updatePropertyValue(updateAdminShopPropertyDto));

        //then
        then(updateAdminShopPropertyDtoValidator).should(times(1)).validate(captor2.capture());
        then(updateAdminShopPropertyDtoValidator).should(times(1)).hasErrors();
        then(updateAdminShopPropertyDtoValidator).shouldHaveNoMoreInteractions();


        then(adminShopPropertyRepository).should(times(1)).findByProperty(captor.capture());
        then(adminShopPropertyRepository).shouldHaveNoMoreInteractions();


        assertThat(exception.getMessage(), is(equalTo(expectedExceptionMessage)));
        assertThat(captor.getValue().toString(), is(equalTo(updateAdminShopPropertyDto.getPropertyName())));
        assertThat(captor2.getValue(), is(equalTo(updateAdminShopPropertyDto)));

    }

    @Test
    @DisplayName("update - valid argument - successful update")
    void test14() {


        //given
        var updateAdminShopPropertyDto = UpdateAdminShopPropertyDto.builder().propertyName("X").value(new BigDecimal("0.3")).build();

        var captor = ArgumentCaptor.forClass(AdminShopPropertyName.class);
        var captor2 = ArgumentCaptor.forClass(UpdateAdminShopPropertyDto.class);


        given(updateAdminShopPropertyDtoValidator.validate(any(UpdateAdminShopPropertyDto.class)))
                .willReturn(Collections.emptyMap());

        given(updateAdminShopPropertyDtoValidator.hasErrors())
                .willReturn(false);

        given(adminShopPropertyRepository.findByProperty(any(AdminShopPropertyName.class)))
                .willAnswer((Answer<Optional<AdminShopProperty>>) invocationOnMock -> Optional.of(AdminShopProperty.builder()
                        .id(1L)
                        .property(invocationOnMock.getArgument(0))
                        .value(new BigDecimal("0.3"))
                        .build()));

        //when
        Long result = Assertions
                .assertDoesNotThrow(() ->
                        adminShopPropertyService.updatePropertyValue(updateAdminShopPropertyDto));

        //then
        then(updateAdminShopPropertyDtoValidator).should(times(1)).validate(captor2.capture());
        then(updateAdminShopPropertyDtoValidator).should(times(1)).hasErrors();
        then(updateAdminShopPropertyDtoValidator).shouldHaveNoMoreInteractions();


        then(adminShopPropertyRepository).should(times(1)).findByProperty(captor.capture());
        then(adminShopPropertyRepository).shouldHaveNoMoreInteractions();

        assertThat(result, is(1L));
        assertThat(captor.getValue().toString(), is(equalTo(updateAdminShopPropertyDto.getPropertyName())));
        assertThat(captor2.getValue(), is(equalTo(updateAdminShopPropertyDto)));

    }
}
