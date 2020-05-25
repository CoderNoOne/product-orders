package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.CustomerRepository;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.ShopRepository;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.dto.CreateProductOrderDto;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@SessionScope
@Component
@RequiredArgsConstructor
public class CreateProductOrderDtoValidator extends AbstractValidator<CreateProductOrderDto> {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final StockRepository stockRepository;
    private final ShopRepository shopRepository;

    @Override
    public Map<String, String> validate(CreateProductOrderDto createProductOrderDto) {

        errors.clear();

        if (Objects.isNull(createProductOrderDto)) {
            errors.put("CreateProductOrderDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createProductOrderDto.getProductId())) {
            errors.put("Product id", "is null");
        } else if (!doProductExist(createProductOrderDto.getProductId())) {
            errors.put("Product object", "No product with id: " + createProductOrderDto.getProductId());
        }

        if (Objects.isNull(createProductOrderDto.getCustomerUsername())) {
            errors.put("Customer username", "is null");
        } else if (!doCustomerExist(createProductOrderDto.getCustomerUsername())) {
            errors.put("Customer object", "No customer with username: " + createProductOrderDto.getCustomerUsername());
        }

        if (!isDeliveryAddressValid(createProductOrderDto.getDeliveryAddress())) {
            errors.put("Delivery address", "is not specified");
        }

        if (Objects.isNull(createProductOrderDto.getPaymentDeadline())) {
            errors.put("Payment deadline", "is null");
        } else if (!isPaymentDeadlineValid(createProductOrderDto.getPaymentDeadline())) {
            errors.put("Payment deadline", "must take place in the future");
        }

        if (Objects.isNull(createProductOrderDto.getDiscount())) {
            errors.put("Discount", "is null");
        } else if (!isDiscountValid(createProductOrderDto.getDiscount())) {
            errors.put("Discount value", "should be in the range <0,100>");
        }

        if (Objects.isNull(createProductOrderDto.getProductStockQuantity())) {
            errors.put("ProductStockQuantity object", "is null");
        } else {

            if (!areProductStockQuantityKeysValid(createProductOrderDto.getProductStockQuantity().keySet())) {
                errors.put("Stock ids", "Stock ids have to be numbers only");
            } else if (!doAllStockBelongToTheSameShop(createProductOrderDto.getProductStockQuantity().keySet(), createProductOrderDto.getShopId())) {
                errors.put("Stock objects", "All stocks must belong to the same shop with id: " + createProductOrderDto.getShopId());
            }

            if (!areProductStockQuantityValuesValid(createProductOrderDto.getProductStockQuantity().values())) {
                errors.put("Product quantity", "has to be positive numbers");
            }

        }

        if (Objects.isNull(createProductOrderDto.getShopId())) {
            errors.put("Shop id", "is null");
        } else if (!doShopExist(createProductOrderDto.getShopId())) {
            errors.put("Shop object", "No shop with id: " + createProductOrderDto.getShopId());
        }

        return errors;
    }

    private boolean doShopExist(Long shopId) {
        return shopRepository.findOne(shopId).isPresent();
    }

    private boolean doAllStockBelongToTheSameShop(Set<String> stockIds, Long shopId) {
        List<Long> stockIdsList = stockIds.stream().map(Long::valueOf).distinct().collect(Collectors.toList());
        return stockRepository.doAllStocksBelongToTheSameShop(shopId, stockIdsList);
    }

    private boolean areProductStockQuantityValuesValid(Collection<Integer> values) {
        return values.stream().allMatch(number -> number > 0);
    }

    private boolean areProductStockQuantityKeysValid(Set<String> keys) {
        return keys.stream().allMatch(key -> key.matches("\\d"));
    }

    private boolean isDiscountValid(BigDecimal discount) {
        return discount.compareTo(BigDecimal.ZERO) >= 0 &&
                discount.compareTo(new BigDecimal("100")) <= 0;
    }

    private boolean isPaymentDeadlineValid(LocalDate paymentDeadline) {
        return paymentDeadline.compareTo(LocalDate.now()) > 0;
    }

    private boolean isDeliveryAddressValid(String deliveryAddress) {
        return Objects.nonNull(deliveryAddress) && Strings.isNotBlank(deliveryAddress);
    }

    private boolean doCustomerExist(String customerUsername) {
        return customerRepository.findByUsername(customerUsername).isPresent();
    }

    private boolean doProductExist(Long productId) {
        return productRepository.findOne(productId).isPresent();
    }
}
