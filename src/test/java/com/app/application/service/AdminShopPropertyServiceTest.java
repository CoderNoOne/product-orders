package com.app.application.service;

import com.app.application.validators.impl.CreateAdminShopPropertyDtoValidator;
import com.app.domain.entity.AdminShopProperty;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.domain.repository.AdminShopPropertyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class AdminShopPropertyServiceTest {

    @Mock
    private CreateAdminShopPropertyDtoValidator createAdminShopPropertyDtoValidator;

    @Mock
    private AdminShopPropertyRepository adminShopPropertyRepository;

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
}
