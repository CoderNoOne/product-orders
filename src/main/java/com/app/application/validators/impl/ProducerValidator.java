package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.ProducerRepository;
import com.app.infrastructure.dto.ProducerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@SessionScope
public class ProducerValidator extends AbstractValidator<ProducerDto> {

    private final ProducerRepository producerRepository;

    @Override
    public Map<String, String> validate(ProducerDto producerDto) {

        errors.clear();
        if (Objects.isNull(producerDto)) {
            super.errors.put("product object", "is null");
            return errors;
        }

        if (!isTradeValid(producerDto.getTradeName())) {
            errors.put("TradeName", "Trade name is not correct");
        }

        if (!isNameValid(producerDto.getName())) {
            errors.put("Name", "Name is not correct");
        }


        return errors;
    }

    private boolean isTradeValid(String tradeName) {
        return Objects.nonNull(tradeName);
    }

    private boolean isNameValid(String producerName) {
        return Objects.nonNull(producerName) &&
                producerRepository.findByName(producerName).isEmpty();

    }
}
