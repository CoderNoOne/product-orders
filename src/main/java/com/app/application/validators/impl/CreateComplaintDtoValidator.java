package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.entity.ProductOrder;
import com.app.domain.repository.ProductOrderRepository;
import com.app.domain.enums.DamageType;
import com.app.domain.enums.ProductOrderStatus;
import com.app.infrastructure.dto.CreateComplaintDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@SessionScope
@RequiredArgsConstructor
public class CreateComplaintDtoValidator extends AbstractValidator<CreateComplaintDto> {

    private final ProductOrderRepository productOrderRepository;

    @Override
    public Map<String, String> validate(CreateComplaintDto createComplaintDto) {

        errors.clear();

        if (Objects.isNull(createComplaintDto)) {
            errors.put("CreateComplaintDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createComplaintDto.getProductOrderId())) {
            errors.put("ProductOrder id", "is null");
        } else if (!doProductOrderExist(createComplaintDto.getProductOrderId())) {
            errors.put("Product order", "No product order with provided id");
        } else if (!productOrderStatusIsNotDone(createComplaintDto.getProductOrderId())) {
            errors.put("Product order", "Status should be Done. You cannot make a complaint");
        }

        if (Objects.isNull(createComplaintDto.getDamageType())) {
            errors.put("Damage type", "is null");
        } else if (!isDamageTypeValid(createComplaintDto.getDamageType())) {
            errors.put("Damage type", "is not valid");
        }

        return errors;
    }

    private boolean productOrderStatusIsNotDone(Long productOrderId) {
        Optional<ProductOrder> optional = productOrderRepository.findOne(productOrderId);

        return optional.isPresent() &&
                Objects.equals(optional.get().getStatus(), ProductOrderStatus.DONE);
    }

    private boolean isDamageTypeValid(String damageType) {
        return Arrays.stream(DamageType.values())
                .map(DamageType::name)
                .anyMatch(damageTypeEnumName -> Objects.equals(damageTypeEnumName, damageType));
    }

    private boolean doProductOrderExist(Long productOrderId) {
        return productOrderRepository.findOne(productOrderId).isPresent();
    }
}
