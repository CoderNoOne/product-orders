package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.infrastructure.dto.ProductOrderFilteringCriteriaDto;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Component
@SessionScope
public class ProductOrderFilteringCriteriaDtoValidator extends AbstractValidator<ProductOrderFilteringCriteriaDto> {

    @Override
    public Map<String, String> validate(ProductOrderFilteringCriteriaDto productOrderFilteringCriteriaDto) {

        errors.clear();

        if (Objects.isNull(productOrderFilteringCriteriaDto)) {
            errors.put("ProductOrderFilteringCriteriaDto object", "is null");
            return errors;
        }

        if (!isMinPriceValid(productOrderFilteringCriteriaDto.getMinPrice())) {
            errors.put("Product minPrice", "Product minPrice have to be greater than 0");
        }

        if (!isMaxPriceValid(productOrderFilteringCriteriaDto.getMaxPrice())) {
            errors.put("Product maxPrice", "Product maxPrice have to be greater than 0");
        }

        if (Objects.nonNull(productOrderFilteringCriteriaDto.getMaxPrice())
                && Objects.nonNull(productOrderFilteringCriteriaDto.getMinPrice())
                && !isMaxPriceGreaterThanOrEqualToMinPrice(productOrderFilteringCriteriaDto.getMinPrice(), productOrderFilteringCriteriaDto.getMaxPrice())) {

            errors.put("Max and min product price", "Min price cannot be greater than max price");
        }

        if (!isFromDateValid(productOrderFilteringCriteriaDto.getFromDate())) {
            errors.put("Order from date", "Order from date cannot be in the future");
        }

        if (!isToDateValid(productOrderFilteringCriteriaDto.getToDate())) {
            errors.put("Order to date", "Order to date cannot be in the future");
        }

        if (Objects.nonNull(productOrderFilteringCriteriaDto.getFromDate()) &&
                Objects.nonNull(productOrderFilteringCriteriaDto.getToDate()) &&
                !isFromDateNotAfterToDate(productOrderFilteringCriteriaDto.getFromDate(), productOrderFilteringCriteriaDto.getToDate())
        ) {
            errors.put("Order from and to date", "Order fromDate cannot be after toDate");
        }


        if (!isMinQuantityValid(productOrderFilteringCriteriaDto.getMinQuantity())) {
            errors.put("Min quantity", "Min quantity have to be greater than 0");
        }

        if (!isMaxQuantityValid(productOrderFilteringCriteriaDto.getMaxQuantity())) {
            errors.put("Max quantity", "Max quantity have to greater than 0");
        }

        if (Objects.nonNull(productOrderFilteringCriteriaDto.getMaxQuantity()) &&
                Objects.nonNull(productOrderFilteringCriteriaDto.getMinQuantity()) &&
                !isMinQuantityNotGreaterThanMaxQuantity(productOrderFilteringCriteriaDto.getMinQuantity(), productOrderFilteringCriteriaDto.getMaxQuantity())) {
            errors.put("Max min quantity", "Max quantity has to be greater than or equal to min quantity");
        }

        if (!isCategoryValid(productOrderFilteringCriteriaDto.getCategory())) {
            errors.put("Product category", "Product category is not valid");
        }

        if (!isProductNameValid(productOrderFilteringCriteriaDto.getProductName())) {
            errors.put("Product name", "Product name is not valid");
        }

        if (!isProducerNameValid(productOrderFilteringCriteriaDto.getProducerName())) {
            errors.put("Producer name", "Producer name is not valid");
        }

        return errors;
    }

    private boolean isProducerNameValid(String producerName) {
        return Objects.isNull(producerName) || Strings.isNotBlank(producerName);
    }

    private boolean isProductNameValid(String productName) {
        return Objects.isNull(productName) || Strings.isNotBlank(productName);
    }

    private boolean isCategoryValid(String category) {
        return Objects.isNull(category) || Strings.isNotBlank(category);
    }

    private boolean isMinQuantityNotGreaterThanMaxQuantity(Integer minQuantity, Integer maxQuantity) {
        return minQuantity <= maxQuantity;
    }

    private boolean isMaxQuantityValid(Integer maxQuantity) {
        return Objects.isNull(maxQuantity) || maxQuantity > 0;
    }

    private boolean isMinQuantityValid(Integer minQuantity) {
        return Objects.isNull(minQuantity) || minQuantity > 0;
    }

    private boolean isFromDateNotAfterToDate(LocalDate fromDate, LocalDate toDate) {
        return fromDate.compareTo(toDate) <= 0;
    }

    private boolean isToDateValid(LocalDate toDate) {
        return Objects.isNull(toDate) || toDate.compareTo(LocalDate.now()) <= 0;
    }


    private boolean isFromDateValid(LocalDate fromDate) {
        return Objects.isNull(fromDate) || fromDate.compareTo(LocalDate.now()) <= 0;
    }


    private boolean isMaxPriceGreaterThanOrEqualToMinPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        return minPrice.compareTo(maxPrice) <= 0;
    }

    private boolean isMaxPriceValid(BigDecimal maxPrice) {
        return Objects.isNull(maxPrice) || maxPrice.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isMinPriceValid(BigDecimal minPrice) {
        return Objects.isNull(minPrice) || minPrice.compareTo(BigDecimal.ZERO) > 0;
    }
}
