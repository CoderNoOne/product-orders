package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.enums.ProposalStatus;
import com.app.domain.repository.*;
import com.app.infrastructure.dto.CreateProductOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.*;

@SessionScope
@Component
@RequiredArgsConstructor
public class CreateProductOrderDtoValidator extends AbstractValidator<CreateProductOrderDto> {

    private final ProductOrderProposalRepository productOrderProposalRepository;
    private final ManagerRepository managerRepository;

    @Override
    public Map<String, String> validate(CreateProductOrderDto createProductOrderDto) {

        errors.clear();

        if (Objects.isNull(createProductOrderDto)) {
            errors.put("CreateProductOrderDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createProductOrderDto.getManagerUsername())) {
            errors.put("Manager username", "is null");
            return errors;
        } else if (!doManagerExist(createProductOrderDto.getManagerUsername())) {
            errors.put("Manager username", "No manager with username: " + createProductOrderDto.getManagerUsername());
        }


        if (Objects.isNull(createProductOrderDto.getAcceptedProductOrderProposalId())) {
            errors.put("ProductOrderProposal id", "is null");
        } else if (!isProductOrderProposalValid(createProductOrderDto.getAcceptedProductOrderProposalId(), createProductOrderDto.getManagerUsername())) {
            errors.put("ProductOrderProposal object", "must exist, by managed by you, and have status ACCEPTED");
        }

        return errors;
}

    private boolean doManagerExist(String managerUsername) {
        return managerRepository.findByUsername(managerUsername).isPresent();
    }

    private boolean isProductOrderProposalValid(Long productOrderProposalId, String managerUsername) {
        return productOrderProposalRepository.findByIdAndManagerUsernameAndStatus(productOrderProposalId, managerUsername, ProposalStatus.ACCEPTED)
                .isPresent();

    }


}
