package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.infrastructure.dto.UpdateProductDto;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Component
@SessionScope
public class UpdateProductDtoValidator extends AbstractValidator<UpdateProductDto> {

    @Override
    public Map<String, String> validate(UpdateProductDto updateProductDto) {

        errors.clear();

        if (Objects.isNull(updateProductDto)) {
            errors.put("UpdateProductDto object", "is null");
            return errors;
        }

        if (!isPriceValid(updateProductDto.getPrice())) {
            errors.put("Price", "is not valid");
        }

        if (!productNameIsValid(updateProductDto.getName())) {
            errors.put("Product name", "is not valid");
        }

        return errors;
    }

    private boolean productNameIsValid(String name) {
        return Objects.isNull(name) ||
                Strings.isNotBlank(name);
    }

    private boolean isPriceValid(BigDecimal price) {
        return Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) > 0;
    }
}
