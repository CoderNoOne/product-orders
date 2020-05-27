package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.CustomerRepository;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.ShopRepository;
import com.app.infrastructure.dto.CreateManagerProductOrderProposalDto;
import com.app.infrastructure.dto.CreateProposalRemarkDto;
import com.app.infrastructure.dto.ProposalRemarkDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@SessionScope
@Component
@RequiredArgsConstructor
public class CreateManagerProductOrderProposalDtoValidator extends AbstractValidator<CreateManagerProductOrderProposalDto> {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Map<String, String> validate(CreateManagerProductOrderProposalDto createManagerProductOrderProposalDto) {

        errors.clear();

        if (Objects.isNull(createManagerProductOrderProposalDto)) {
            errors.put("CreateProductOrderProposalByManagerDto", "is null");
            return errors;
        }

        if (Objects.isNull(createManagerProductOrderProposalDto.getProductInfo())) {
            errors.put("Product info", "is null");
        } else {

            String productName = createManagerProductOrderProposalDto.getProductInfo().getName();
            String producerName = createManagerProductOrderProposalDto.getProductInfo().getProducerName();

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

        if (Objects.isNull(createManagerProductOrderProposalDto.getShopName())) {
            errors.put("ShopName", "is null");
        } else if (!doShopExist(createManagerProductOrderProposalDto.getShopName())) {
            errors.put("Shop object", "No shop with name: " + createManagerProductOrderProposalDto.getShopName());
        }

        if (Objects.isNull(createManagerProductOrderProposalDto.getQuantity())) {
            errors.put("Quantity", "is null");
        } else if (!isQuantityValid(createManagerProductOrderProposalDto.getQuantity())) {
            errors.put("Quantity", "should be greater than 0");
        }

        if (Objects.nonNull(createManagerProductOrderProposalDto.getRemark()) && !isRemarkValid(createManagerProductOrderProposalDto.getRemark())) {
            errors.put("Remark", "is not valid");
        }

        if (Objects.isNull(createManagerProductOrderProposalDto.getDaysFromOrderToPaymentDeadline())) {
            errors.put("Days from order to payment deadline", "is null");
        } else if (!isDaysFromOrderToPaymentDeadlineValid(createManagerProductOrderProposalDto.getDaysFromOrderToPaymentDeadline())) {
            errors.put("Days from order to payment deadline", "should be greater than 0 and less than or equal to 60");
        }

        if (Objects.isNull(createManagerProductOrderProposalDto.getCustomerUsername())) {
            errors.put("Customer username", "is null");
        } else if (!isCustomerPresent(createManagerProductOrderProposalDto.getCustomerUsername())) {
            errors.put("Customer username", "There is no customer with username: " + createManagerProductOrderProposalDto.getCustomerUsername());
        }

        return errors;
    }

    private boolean isCustomerPresent(String customerUsername) {
        return customerRepository.findByUsername(customerUsername).isPresent();
    }

    private boolean isDaysFromOrderToPaymentDeadlineValid(Integer daysFromOrderToPaymentDeadline) {
        return daysFromOrderToPaymentDeadline > 0 && daysFromOrderToPaymentDeadline <= 60;
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
