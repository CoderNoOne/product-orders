package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.ShopRepository;
import com.app.infrastructure.dto.CreateProductOrderProposalByManagerDto;
import com.app.infrastructure.dto.ManagerProposalRemarkDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@SessionScope
@Component
@RequiredArgsConstructor
public class CreateProductOrderProposalByManagerDtoValidator extends AbstractValidator<CreateProductOrderProposalByManagerDto> {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    @Override
    public Map<String, String> validate(CreateProductOrderProposalByManagerDto createProductOrderProposalByManagerDto) {

        errors.clear();

        if (Objects.isNull(createProductOrderProposalByManagerDto)) {
            errors.put("CreateProductOrderProposalByManagerDto", "is null");
            return errors;
        }

        if (Objects.isNull(createProductOrderProposalByManagerDto.getProductInfo())) {
            errors.put("Product info", "is null");
        } else {

            String productName = createProductOrderProposalByManagerDto.getProductInfo().getName();
            String producerName = createProductOrderProposalByManagerDto.getProductInfo().getProducerName();

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

        if (Objects.isNull(createProductOrderProposalByManagerDto.getShopName())) {
            errors.put("ShopName", "is null");
        } else if (!doShopExist(createProductOrderProposalByManagerDto.getShopName())) {
            errors.put("Shop object", "No shop with name: " + createProductOrderProposalByManagerDto.getShopName());
        }

        if (Objects.isNull(createProductOrderProposalByManagerDto.getQuantity())) {
            errors.put("Quantity", "is null");
        } else if (!isQuantityValid(createProductOrderProposalByManagerDto.getQuantity())) {
            errors.put("Quantity", "should be greater than 0");
        }

        if (Objects.nonNull(createProductOrderProposalByManagerDto.getRemark()) && !isRemarkValid(createProductOrderProposalByManagerDto.getRemark())) {
            errors.put("Remark", "is not valid");
        }

        return errors;
    }


    private boolean isRemarkValid(ManagerProposalRemarkDto remark) {
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
