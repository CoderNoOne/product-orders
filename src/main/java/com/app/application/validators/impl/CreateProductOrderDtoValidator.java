package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.StockRepository;
import com.app.domain.repository.ProductRepository;
import com.app.infrastructure.dto.CreateProductOrderDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@Component
@SessionScope
@RequiredArgsConstructor
public class CreateProductOrderDtoValidator extends AbstractValidator<CreateProductOrderDto> {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;

    @Override
    public Map<String, String> validate(CreateProductOrderDto createProductOrderDto) {

        errors.clear();

        if (Objects.isNull(createProductOrderDto)) {
            errors.put("CreateProductOrderDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createProductOrderDto.getProductInfo())) {
            errors.put("Product info", "is null");
        } else {
            if (!isProductNameValid(createProductOrderDto.getProductInfo().getName())) {
                errors.put("Product name", "is not set");
            }

            if (!isProducerNameValid(createProductOrderDto.getProductInfo().getProducerName())) {
                errors.put("Producer name", "is not set");
            }

            if (isProducerNameValid(createProductOrderDto.getProductInfo().getName()) &&
                    isProducerNameValid(createProductOrderDto.getProductInfo().getProducerName()) &&
                    !doProductExist(createProductOrderDto.getProductInfo())) {
                errors.put("Product object", "not exists");
            }

        }

        if (Objects.isNull(createProductOrderDto.getQuantity())) {
            errors.put("Product quantity", "Product quantity is not set");
        } else if (!isProductQuantityValid(createProductOrderDto.getQuantity())) {
            errors.put("Product quantity", "Product quantity should be greater than 0");
        } else if (doStockExists(createProductOrderDto.getStockId()) &&
                doProductExist(createProductOrderDto.getProductInfo()) &&
                !notEnoughProductInStore(createProductOrderDto.getProductInfo(),
                        createProductOrderDto.getQuantity(), createProductOrderDto.getStockId())) {
            errors.put("Product quantity", "There is not enough product quantity in specified store");
        }

        if (Objects.isNull(createProductOrderDto.getStockId())) {
            errors.put("Stock id", "Stock id is not set");
        }

        if (!doStockExists(createProductOrderDto.getStockId())) {
            errors.put("Stock id", "No stock with id: " + createProductOrderDto.getStockId());
        }

        if (Objects.isNull(createProductOrderDto.getDeliveryAddress())) {
            errors.put("Delivery address", "is null");
        } else if (!isDeliveryAddressValid(createProductOrderDto.getDeliveryAddress())) {
            errors.put("Delivery address", "is blank");
        }

        return errors;
    }

    private boolean isDeliveryAddressValid(String deliveryAddress) {

        return Objects.nonNull(deliveryAddress) && Strings.isNotBlank(deliveryAddress);
    }

    private boolean notEnoughProductInStore(ProductInfo productInfo, Integer quantity, Long stockId) {

        return Objects.nonNull(quantity) && Objects.nonNull(stockId) && Objects.nonNull(productInfo) &&
                Objects.nonNull(productInfo.getName()) && Objects.nonNull(productInfo.getProducerName()) &&
                stockRepository.findProductQuantityInStock(productInfo.getName(), productInfo.getProducerName(), stockId) >= quantity;
    }

    private boolean doProductExist(ProductInfo productInfo) {
        return Objects.nonNull(productInfo) &&
                Objects.nonNull(productInfo.getProducerName()) &&
                Objects.nonNull(productInfo.getName()) &&
                productRepository.findByNameAndProducerName(productInfo.getName(), productInfo.getProducerName()).isPresent();
    }

    private boolean isProducerNameValid(String producerName) {
        return Objects.nonNull(producerName) && Strings.isNotBlank(producerName);
    }

    private boolean isProductNameValid(String name) {
        return Objects.nonNull(name) && Strings.isNotBlank(name);
    }

    private boolean doStockExists(Long stockId) {
        return stockRepository.findOne(stockId).isPresent();
    }

    private boolean isProductQuantityValid(Integer quantity) {
        return quantity > 0;
    }
}
