package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.ShopRepository;
import com.app.domain.repository.ProductRepository;
import com.app.infrastructure.dto.createShop.CreateShopDto;
import com.app.infrastructure.dto.createShop.CreateStockDto;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@SessionScope
public class CreateShopDtoValidator extends AbstractValidator<CreateShopDto> {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    @Override
    public Map<String, String> validate(CreateShopDto createShopDto) {

        errors.clear();

        if (Objects.isNull(createShopDto)) {
            super.errors.put("Shop object", "is null");
            return errors;
        }

        if (!isShopNameValid(createShopDto.getName())) {
            errors.put("Shop name", "Shop name is not valid");
        }

        if (!isShopNameUnique(createShopDto.getName())) {
            errors.put("Shop name", "Shop name is not unique");
        }

        if (!isShopBudgetValid(createShopDto.getBudget())) {
            errors.put("Shop budget", "Sho budget is not valid");
        }

        if (!isShopAddressValid(createShopDto.getAddress())) {
            errors.put("Shop address", "Shop address is not valid");
        }

        if (!areStocksValid(createShopDto.getStocks())) {
            errors.put("Shop stocks object", "Shop stocks are not valid");
        }

        if (Objects.nonNull(createShopDto.getStocks()) && !areStockAddressesValid(createShopDto.getStocks())) {
            errors.put("Shop stocks addresses", "At least one stock address is not valid");
        }

        if (Objects.nonNull(createShopDto.getStocks()) && !areStocksAddressesUnique(createShopDto.getStocks())) {
            errors.put("Shop stock addresses", "Stock should be located in different places");
        }


        return errors;
    }

    private boolean isShopNameUnique(String name) {
        return shopRepository.findByName(name).isEmpty();
    }

    private boolean areStocksAddressesUnique(Set<CreateStockDto> stocks) {
        return stocks.stream()
                .map(CreateStockDto::getAddress)
                .distinct().count() == stocks.size();
    }

//    private boolean areStocksProductsValid(Set<CreateStockDto> stocks) {
//        return stocks.stream()
//                .flatMap(createStockDto -> createStockDto.getProductsQuantity().stream().map(ProductQuantityDto::getProductInfo))
//                .allMatch(this::isSingleProductInfoValid);
//    }

//    private boolean isSingleProductInfoValid(ProductInfo productInfo) {
//        return Objects.nonNull(productInfo) &&
//                Objects.nonNull(productInfo.getName()) &&
//                Objects.nonNull(productInfo.getProducerName()) &&
//                productRepository.findByNameAndProducerName(productInfo.getName(), productInfo.getProducerName()).isPresent();
//
//
//    }
//
//    private boolean areStocksProductQuantityValid(Set<CreateStockDto> stocks) {
//
//        return stocks.stream()
//                .flatMap(createStockDto -> createStockDto.getProductsQuantity().stream().map(ProductQuantityDto::getQuantity))
//                .allMatch(quantity -> quantity > 0);
//    }

    private boolean areStockAddressesValid(Set<CreateStockDto> stocks) {
        return stocks.stream()
                .map(CreateStockDto::getAddress)
                .allMatch(address -> Objects.nonNull(address) && Strings.isNotBlank(address));
    }

    private boolean areStocksValid(Set<CreateStockDto> stocks) {
        return Objects.isNull(stocks) ||
                stocks.stream().allMatch(Objects::nonNull) &&
                        stocks.size() >= 1;
    }

    private boolean isShopAddressValid(String address) {
        return Objects.nonNull(address) && Strings.isNotBlank(address);
    }

    private boolean isShopBudgetValid(BigDecimal budget) {
        return Objects.nonNull(budget) &&
                budget.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isShopNameValid(String name) {
        return Objects.nonNull(name) && Strings.isNotBlank(name);
    }
}
