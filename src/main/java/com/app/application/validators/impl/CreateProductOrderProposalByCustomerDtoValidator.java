package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.ShopRepository;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.dto.CreateProductOrderProposalByCustomerDto;
import com.app.infrastructure.dto.CreateProposalRemarkDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@SessionScope
@Component
@RequiredArgsConstructor
public class CreateProductOrderProposalByCustomerDtoValidator extends AbstractValidator<CreateProductOrderProposalByCustomerDto> {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    @Override
    public Map<String, String> validate(CreateProductOrderProposalByCustomerDto createProductOrderProposalByCustomerDto) {

        errors.clear();

        if (Objects.isNull(createProductOrderProposalByCustomerDto)) {
            errors.put("CreateProductOrderProposalByCustomerDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createProductOrderProposalByCustomerDto.getProductInfo())) {
            errors.put("Product info", "is null");
        } else {

            String productName = createProductOrderProposalByCustomerDto.getProductInfo().getName();
            String producerName = createProductOrderProposalByCustomerDto.getProductInfo().getProducerName();

            if (Objects.isNull(productName)) {
                errors.put("Product name", "is null");
            }

            if (Objects.isNull(producerName)) {
                errors.put("Producer name", "is  null");
            }

            if (Objects.nonNull(productName)
                    && Objects.nonNull(producerName)
                    && productRepository.findByNameAndProducerName(productName, producerName).isEmpty()) {
                errors.put("Product object", "No product with name: " + productName + " and producerName: " + producerName);
            }
        }

        if (Objects.isNull(createProductOrderProposalByCustomerDto.getShopName())) {
            errors.put("ShopName", "is null");
        } else if (!doShopExist(createProductOrderProposalByCustomerDto.getShopName())) {
            errors.put("Shop object", "No shop with name: " + createProductOrderProposalByCustomerDto.getShopName());
        }

        if (Objects.isNull(createProductOrderProposalByCustomerDto.getQuantity())) {
            errors.put("Quantity", "is null");
        } else if (!isQuantityValid(createProductOrderProposalByCustomerDto.getQuantity())) {
            errors.put("Quantity", "should be greater than 0");
        }

        if (Objects.nonNull(createProductOrderProposalByCustomerDto.getRemark()) && !isRemarkValid(createProductOrderProposalByCustomerDto.getRemark())) {
            errors.put("Remark", "is not valid");
        }

        return errors;
    }

    private boolean isRemarkValid(CreateProposalRemarkDto remark) {
        return Objects.nonNull(remark.getTittle())
                && remark.getTittle().length() <= 255
                && Objects.nonNull(remark.getContent())
                && remark.getContent().length() <= 2000;
    }

    private boolean isQuantityValid(Integer quantity) {
        return quantity > 0;
    }

    private boolean doShopExist(String shopName) {
        return shopRepository.findByName(shopName).isPresent();
    }
}
