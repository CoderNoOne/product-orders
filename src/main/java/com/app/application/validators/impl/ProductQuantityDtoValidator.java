package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.ProductRepository;
import com.app.infrastructure.dto.createShop.ProductInfo;
import com.app.infrastructure.dto.createShop.ProductQuantityDto;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@SessionScope
public class ProductQuantityDtoValidator extends AbstractValidator<ProductQuantityDto> {

    private final ProductRepository productRepository;

    @Override
    public Map<String, String> validate(ProductQuantityDto productQuantityDto) {

        errors.clear();

        if (Objects.isNull(productQuantityDto)) {
            errors.put("ProductQuantityDto object", "is null");
            return errors;
        }

        if (!isProductInfoNotNull(productQuantityDto.getProductInfo())) {
            errors.put("ProductInfo object", "is null");

        } else {

            if (!isProductNameValid(productQuantityDto.getProductInfo().getName())) {
                errors.put("Product name", "is not specified");
            }
            if (!isProducerNameValid(productQuantityDto.getProductInfo().getProducerName())) {
                errors.put("Producer name", "is not specified");

            } else if (!doProductExists(productQuantityDto.getProductInfo())) {
                errors.put("Product object", "do not exist");
            }

            if (!isProductQuantityValid(productQuantityDto.getQuantity())) {
                errors.put("Product quantity", "must be greater than 0");
            }
        }

        return errors;

    }


    private boolean isProductQuantityValid(Integer quantity) {
        return Objects.nonNull(quantity) && quantity > 0;
    }

    private boolean isProducerNameValid(String producerName) {
        return Objects.nonNull(producerName) && Strings.isNotBlank(producerName);
    }

    private boolean isProductNameValid(String name) {
        return Objects.nonNull(name) && Strings.isNotBlank(name);
    }

    private boolean doProductExists(ProductInfo productInfo) {

        return productRepository.findByNameAndProducerName(
                productInfo.getName(),
                productInfo.getProducerName()
        ).isPresent();
    }

    private boolean isProductInfoNotNull(ProductInfo productInfo) {
        return Objects.nonNull(productInfo);
    }

}
