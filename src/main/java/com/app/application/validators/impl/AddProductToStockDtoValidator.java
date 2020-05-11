package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.AdminShopPropertyRepository;
import com.app.domain.repository.StockRepository;
import com.app.domain.repository.ProductRepository;
import com.app.infrastructure.dto.AddProductToStockDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@Component
@SessionScope
@RequiredArgsConstructor
public class AddProductToStockDtoValidator extends AbstractValidator<AddProductToStockDto> {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final AdminShopPropertyRepository adminShopPropertyRepository;

    @Override
    public Map<String, String> validate(AddProductToStockDto addProductToStockDto) {

        errors.clear();

        if (Objects.isNull(addProductToStockDto)) {
            errors.put("AddProductToStockDto object", "is null");
            return errors;
        }

        if (Objects.isNull(addProductToStockDto.getProductInfo())) {
            errors.put("ProductInfo object", "is null");
        } else {

            if (Objects.isNull(addProductToStockDto.getProductInfo().getName())) {
                errors.put("Product name", "is null");
            }

            if (Objects.isNull(addProductToStockDto.getProductInfo().getProducerName())) {
                errors.put("Producer name", "is null");
            }

            if (Objects.nonNull(addProductToStockDto.getProductInfo().getProducerName())
                    && Objects.nonNull(addProductToStockDto.getProductInfo().getProducerName())
                    && !doesProductExist(addProductToStockDto.getProductInfo())
            ) {
                errors.put("Product", "does not exist");
            }
        }

        if (Objects.isNull(addProductToStockDto.getStockId())) {
            errors.put("Stock id", "is null");
        } else if (!doesStockExist(addProductToStockDto.getStockId())) {
            errors.put("Stock id", "No stock with id: " + addProductToStockDto.getStockId());
        }

        if (Objects.isNull(addProductToStockDto.getQuantity())) {
            errors.put("Product quantity", "is null");
        } else if (!isQuantityValid(addProductToStockDto.getQuantity())) {
            errors.put("Product quantity", "Quantity has to be greater than 0");
        }

        return errors;
    }

    private boolean isQuantityValid(Integer quantity) {
        return quantity > 0;
    }

    private boolean doesStockExist(Long stockId) {
        return stockRepository.findOne(stockId).isPresent();
    }


    private boolean doesProductExist(ProductInfo productInfo) {
        return productRepository.findByNameAndProducerName(productInfo.getName(), productInfo.getProducerName()).isPresent();
    }
}
