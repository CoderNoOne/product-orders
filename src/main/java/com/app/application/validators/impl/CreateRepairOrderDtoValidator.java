package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.infrastructure.dto.CreateRepairOrderDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Component
@SessionScope
public class CreateRepairOrderDtoValidator extends AbstractValidator<CreateRepairOrderDto> {

    @Override
    public Map<String, String> validate(CreateRepairOrderDto createRepairOrderDto) {

        errors.clear();

        if (Objects.isNull(createRepairOrderDto)) {
            errors.put("CreateRepairOrderDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createRepairOrderDto.getComplaintId())) {
            errors.put("Complaint id", "is null");
        }

        if (Objects.isNull(createRepairOrderDto.getCompletionDate())) {
            errors.put("Completion date", "is null");
        } else if (!isCompletionDateValid(createRepairOrderDto.getCompletionDate())) {
            errors.put("Completion date", "is not valid");
        }

        if (Objects.isNull(createRepairOrderDto.getRepairCosts())) {
            errors.put("Repair costs", "is null");
        } else if (!isRepairCostsValid(createRepairOrderDto.getRepairCosts())) {
            errors.put("Repair costs", "should  be greater than or equal to 0");
        }

        return errors;
    }

    private boolean isRepairCostsValid(BigDecimal repairCosts) {

        return repairCosts.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isCompletionDateValid(LocalDate completionDate) {
        return completionDate.compareTo(LocalDate.now()) > 0;
    }


}
