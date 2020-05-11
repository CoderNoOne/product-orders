package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.infrastructure.dto.OrderDateBoundaryDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Component
@SessionScope
public class OrderDateBoundaryDtoValidator extends AbstractValidator<OrderDateBoundaryDto> {

    @Override
    public Map<String, String> validate(OrderDateBoundaryDto orderDateBoundaryDto) {
        errors.clear();

        if (Objects.isNull(orderDateBoundaryDto)) {
            errors.put("OrderDateBoundaryDto object", "is null");
            return errors;
        }

        if (!areDatesValid(orderDateBoundaryDto.getFrom(), orderDateBoundaryDto.getTo())) {
            errors.put("Dates", "FromDate should be before toDate");
        } else if (Objects.isNull(orderDateBoundaryDto.getFrom()) && Objects.isNull(orderDateBoundaryDto.getTo())) {
            errors.put("Dates", "You need to specify at least one of the parameters: from, to");
        }

        return errors;
    }

    private boolean areDatesValid(LocalDate from, LocalDate to) {
        return (Objects.isNull(from) || Objects.isNull(to)) || from.compareTo(to) <= 0;
    }
}
