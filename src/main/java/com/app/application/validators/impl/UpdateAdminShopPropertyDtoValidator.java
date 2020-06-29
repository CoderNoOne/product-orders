package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.AdminShopPropertyRepository;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.infrastructure.dto.UpdateAdminShopPropertyDto;
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
public class UpdateAdminShopPropertyDtoValidator extends AbstractValidator<UpdateAdminShopPropertyDto> {

    private final AdminShopPropertyRepository adminShopPropertyRepository;

    @Override
    public Map<String, String> validate(UpdateAdminShopPropertyDto updateAdminShopPropertyDto) {

        errors.clear();

        if (Objects.isNull(updateAdminShopPropertyDto)) {
            errors.put("UpdateAdminShopPropertyDto object", "is null");
            return errors;
        }

        if (Objects.isNull(updateAdminShopPropertyDto.getPropertyName())) {
            errors.put("Property name", "is null");
        } else if (!isPropertyNameValid(updateAdminShopPropertyDto.getPropertyName())) {
            errors.put("Property name", "is not valid");
        } else if (!isPropertyPersisted(updateAdminShopPropertyDto.getPropertyName())) {
            errors.put("Property name", "is not persisted");
        }

        if (Objects.isNull(updateAdminShopPropertyDto.getValue())) {
            errors.put("Value", "is null");
        } else if (!isValueValid(updateAdminShopPropertyDto.getValue())) {
            errors.put("Value", "is not valid");
        }

        return errors;
    }

    private boolean isValueValid(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) > 0 &&
                value.compareTo(new BigDecimal("100")) < 0;
    }

    private boolean isPropertyPersisted(String propertyName) {
        return adminShopPropertyRepository.findByProperty(AdminShopPropertyName.valueOf(propertyName)).isPresent();
    }

    private boolean isPropertyNameValid(String propertyName) {
        return Arrays.stream(AdminShopPropertyName.values()).map(AdminShopPropertyName::name)
                .anyMatch(adminProperty -> Objects.equals(adminProperty, propertyName));
    }
}
