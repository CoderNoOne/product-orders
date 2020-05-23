package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.CustomerRepository;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.ShopRepository;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.dto.CreateProductOrderDto2;
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
public class CreateProductOrderDto2Validator extends AbstractValidator<CreateProductOrderDto2> {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final StockRepository stockRepository;
    private final ShopRepository shopRepository;

    @Override
    public Map<String, String> validate(CreateProductOrderDto2 createProductOrderDto2) {

        errors.clear();

        if (Objects.isNull(createProductOrderDto2)) {
            errors.put("CreateProductOrderDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createProductOrderDto2.getProductId())) {
            errors.put("Product id", "is null");
        } else if (!doProductExist(createProductOrderDto2.getProductId())) {
            errors.put("Product object", "No product with id: " + createProductOrderDto2.getProductId());
        }

        if (Objects.isNull(createProductOrderDto2.getCustomerUsername())) {
            errors.put("Customer username", "is null");
        } else if (!doCustomerExist(createProductOrderDto2.getCustomerUsername())) {
            errors.put("Customer object", "No customer with username: " + createProductOrderDto2.getCustomerUsername());
        }

        if (!isDeliveryAddressValid(createProductOrderDto2.getDeliveryAddress())) {
            errors.put("Delivery address", "is not specified");
        }

        if (Objects.isNull(createProductOrderDto2.getPaymentDeadline())) {
            errors.put("Payment deadline", "is null");
        } else if (!isPaymentDeadlineValid(createProductOrderDto2.getPaymentDeadline())) {
            errors.put("Payment deadline", "must take place in the future");
        }

        if (Objects.isNull(createProductOrderDto2.getDiscount())) {
            errors.put("Discount", "is null");
        } else if (!isDiscountValid(createProductOrderDto2.getDiscount())) {
            errors.put("Discount value", "should be in the range <0,100>");
        }

        if (Objects.isNull(createProductOrderDto2.getProductStockQuantity())) {
            errors.put("ProductStockQuantity object", "is null");
        } else {

            if (!areProductStockQuantityKeysValid(createProductOrderDto2.getProductStockQuantity().keySet())) {
                errors.put("Stock ids", "Stock ids have to be numbers only");
            } else if (!doAllStockBelongToTheSameShop(createProductOrderDto2.getProductStockQuantity().keySet(), createProductOrderDto2.getShopId())) {
                errors.put("Stock objects", "All stocks must belong to the same shop with id: " + createProductOrderDto2.getShopId());
            }

            if (!areProductStockQuantityValuesValid(createProductOrderDto2.getProductStockQuantity().values())) {
                errors.put("Product quantity", "has to be positive numbers");
            }

        }

        if (Objects.isNull(createProductOrderDto2.getShopId())) {
            errors.put("Shop id", "is null");
        } else if (!doShopExist(createProductOrderDto2.getShopId())) {
            errors.put("Shop object", "No shop with id: " + createProductOrderDto2.getShopId());
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
