package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.ShopRepository;
import com.app.infrastructure.dto.UpdateShopDto;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@SessionScope
public class UpdateShopDtoValidator extends AbstractValidator<UpdateShopDto> {

    private final ShopRepository shopRepository;

    @Override
    public Map<String, String> validate(UpdateShopDto updateShopDto) {

        errors.clear();

        if (Objects.isNull(updateShopDto)) {
            errors.put("UpdateShopDto object", "is null");
            return errors;
        }

        if (!isNameValid(updateShopDto.getName())) {
            errors.put("UpdateShopDto name", "is not valid");
        }

        if (!isBudgetValid(updateShopDto.getBudget())) {
            errors.put("UpdateShopDto budget", "is not valid");
        }

        if (!isAddressValid(updateShopDto.getAddress())) {
            errors.put("UpdateShopDto Address", "is not valid");
        }

        return errors;
    }

    private boolean isAddressValid(String address) {
        return Objects.isNull(address) ||
                (Strings.isNotBlank(address));
    }

    private boolean isBudgetValid(BigDecimal budget) {
        return Objects.isNull(budget) ||
                (budget.compareTo(BigDecimal.ZERO) >= 0);
    }

    private boolean isNameValid(String name) {
        return Objects.isNull(name) ||
                (Strings.isNotBlank(name) && shopRepository.findByName(name).isEmpty());
    }
}
