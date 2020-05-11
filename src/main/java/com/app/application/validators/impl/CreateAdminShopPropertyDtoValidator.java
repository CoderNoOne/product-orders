package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.infrastructure.dto.CreateAdminShopPropertyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Component
@SessionScope
@RequiredArgsConstructor
public class CreateAdminShopPropertyDtoValidator extends AbstractValidator<CreateAdminShopPropertyDto> {

    @Override
    public Map<String, String> validate(CreateAdminShopPropertyDto createAdminShopPropertyDto) {

        errors.clear();

        if (Objects.isNull(createAdminShopPropertyDto)) {
            errors.put("CreateAdminShopPropertyDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createAdminShopPropertyDto.getProperty())) {
            errors.put("Property", "is null");
        } else if (!isKeyValid(createAdminShopPropertyDto.getProperty())) {
            errors.put("Property", "is not supported");
        }

        if (Objects.isNull(createAdminShopPropertyDto.getValue())) {
            errors.put("Value", "is null");
        } else if (!isValueValid(createAdminShopPropertyDto.getValue())) {
            errors.put("Value", "is not valid");
        }

        return errors;
    }

    private boolean isValueValid(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) > 0 &&
                value.compareTo(new BigDecimal("100")) < 0;
    }

    private boolean isKeyValid(String property) {
        return Arrays.stream(AdminShopPropertyName.values())
                .map(AdminShopPropertyName::name)
                .anyMatch(adminProperty -> Objects.equals(adminProperty, property));
    }
}
