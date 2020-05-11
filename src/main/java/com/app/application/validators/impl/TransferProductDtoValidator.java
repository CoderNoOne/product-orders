package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.StockRepository;
import com.app.domain.entity.Product;
import com.app.domain.repository.ProductRepository;
import com.app.infrastructure.dto.TransferProductDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@SessionScope
public class TransferProductDtoValidator extends AbstractValidator<TransferProductDto> {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Override
    public Map<String, String> validate(TransferProductDto transferProductDto) {

        errors.clear();

        if (Objects.isNull(transferProductDto)) {
            errors.put("TransferProduct object", "is null");
            return errors;
        }

        if (Objects.isNull(transferProductDto.getQuantity())) {
            errors.put("Product quantity", "must be specified");
        } else if (!isProductQuantityValid(transferProductDto.getQuantity())) {
            errors.put("Product quantity", "must be greater than 0");
        }

        if (Objects.isNull(transferProductDto.getProductInfo())) {
            errors.put("ProductInfo object", "is null");
        } else if (!isProductInfoValid(transferProductDto.getProductInfo())) {
            errors.put("ProductInfo object", "Product and producer name must be defined");
        } else if (!doProductExists(transferProductDto.getProductInfo())) {
            errors.put("ProductInfo object", "Product doesn't exist");
        }

        if (!areStockDefined(transferProductDto.getStockFrom(), transferProductDto.getStockTo())) {
            errors.put("Stock objects", "Stock ids must be specified");
        } else {

            if (Objects.equals(transferProductDto.getStockFrom(), transferProductDto.getStockTo())) {
                errors.put("StockIds", "must be different from each other");
            }

            if (!doStockFromExists(transferProductDto.getStockFrom())) {
                errors.put("StockFrom object", "doesn't exist");
            }

            if (!doStockToExists(transferProductDto.getStockTo())) {
                errors.put("StockTo object", "doesn't exist");
            }

            if (doStockFromExists(transferProductDto.getStockFrom())
                    && doStockToExists(transferProductDto.getStockTo())
                    && !doStocksBelongToTheSameShop(transferProductDto.getStockTo(), transferProductDto.getStockFrom())) {
                errors.put("Stock objects", "Stocks do not belong to the same shop");
            }

            if (doStockFromExists(transferProductDto.getStockFrom())
                    && doStockToExists(transferProductDto.getStockTo())
                    && doStocksBelongToTheSameShop(transferProductDto.getStockTo(), transferProductDto.getStockFrom())
                    && !errors.containsKey("Product quantity")
                    && !errors.containsKey("ProductInfo object")
                    && !isEnoughProductInStoreFrom(transferProductDto.getProductInfo(), transferProductDto.getStockFrom(), transferProductDto.getQuantity())
            ) {
                errors.put("Product stock quantity", "Not enough product in stockFrom");
            }

        }


        return errors;

    }

    private boolean doStocksBelongToTheSameShop(Long stockTo, Long stockFrom) {
        return stockRepository.doStocksBelongToTheSameShop(stockFrom, stockTo);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private boolean isEnoughProductInStoreFrom(ProductInfo productInfo, Long stockFrom, Integer quantity) {

        Product product = productRepository.findByNameAndProducerName(productInfo.getName(), productInfo.getProducerName())
                .get();

        Integer productQuantity = stockRepository.findOne(stockFrom)
                .get()
                .getProductsQuantity().get(product);

        return Objects.nonNull(productQuantity)
                && productQuantity >= quantity;
    }

    private boolean isProductQuantityValid(Integer quantity) {
        return quantity > 0;
    }

    private boolean doStockToExists(Long stockTo) {
        return stockRepository.findOne(stockTo).isPresent();
    }

    private boolean doStockFromExists(Long stockFrom) {
        return stockRepository.findOne(stockFrom).isPresent();
    }

    private boolean areStockDefined(Long stockFrom, Long stockTo) {
        return Objects.nonNull(stockFrom) && Objects.nonNull(stockTo);
    }

    private boolean doProductExists(ProductInfo productInfo) {
        return productRepository.findByNameAndProducerName(productInfo.getName(), productInfo.getProducerName()).isPresent();
    }

    private boolean isProductInfoValid(ProductInfo productInfo) {
        return (Objects.nonNull(productInfo.getName()) && Strings.isNotBlank(productInfo.getName()))
                && (Objects.nonNull(productInfo.getProducerName()) && Strings.isNotBlank(productInfo.getProducerName()));

    }


}
