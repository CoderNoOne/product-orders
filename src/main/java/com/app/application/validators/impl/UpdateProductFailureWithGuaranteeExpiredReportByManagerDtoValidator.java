package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.enums.ProductFailureReportStatus;
import com.app.domain.enums.ProposalSide;
import com.app.domain.repository.CustomerRepository;
import com.app.domain.repository.ManagerRepository;
import com.app.domain.repository.ProductFailureWithGuaranteeExpiredReportRepository;
import com.app.infrastructure.dto.UpdateProductFailureWithGuaranteeExpiredReportByCustomerDto;
import com.app.infrastructure.dto.UpdateProductFailureWithGuaranteeExpiredReportByManagerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@SessionScope
@Component
@RequiredArgsConstructor
public class UpdateProductFailureWithGuaranteeExpiredReportByManagerDtoValidator extends AbstractValidator<UpdateProductFailureWithGuaranteeExpiredReportByManagerDto> {

    private final ProductFailureWithGuaranteeExpiredReportRepository productFailureWithGuaranteeExpiredReportRepository;
    private final ManagerRepository managerRepository;

    @Override
    public Map<String, String> validate(UpdateProductFailureWithGuaranteeExpiredReportByManagerDto updateProductFailureWithGuaranteeExpiredReportByManagerDto) {

        errors.clear();

        if (Objects.isNull(updateProductFailureWithGuaranteeExpiredReportByManagerDto)) {
            errors.put("dto object", "is null");
            return errors;
        }

        if (!isCustomerNameValid(updateProductFailureWithGuaranteeExpiredReportByManagerDto.getManagerUsername())) {
            errors.put("Manager name", "doesn't exist");

        } else {

            if (!doProductFailureExist(updateProductFailureWithGuaranteeExpiredReportByManagerDto.getProductFailureWithGuaranteeExpiredReportId(), updateProductFailureWithGuaranteeExpiredReportByManagerDto.getManagerUsername())) {
                errors.put("Product failure", "doesn't exist");
            } else {

                if (!isProductFailureStatusValid(updateProductFailureWithGuaranteeExpiredReportByManagerDto.getProductFailureWithGuaranteeExpiredReportId())) {
                    errors.put("Status", "should be proposed");
                }

                if (!isProductFailureSideValid(updateProductFailureWithGuaranteeExpiredReportByManagerDto.getProductFailureWithGuaranteeExpiredReportId())) {
                    errors.put("Side", "You cannot reply to your own proposal");
                }
            }
        }

        if (!isValidCompletionDate(updateProductFailureWithGuaranteeExpiredReportByManagerDto.getCompletionDate())) {
            errors.put("Completion date", "should be null or in the future");
        }
        if (!areCostsValid(updateProductFailureWithGuaranteeExpiredReportByManagerDto.getCosts())) {
            errors.put("Costs", "should be null or grater than 0");
        }

        return errors;

    }

    private boolean isProductFailureSideValid(Long id) {

        return productFailureWithGuaranteeExpiredReportRepository
                .findById(id).map(productFailureWithGuaranteeExpiredReport -> productFailureWithGuaranteeExpiredReport.getSide().equals(ProposalSide.CUSTOMER))
                .orElseGet(() -> true);
    }

    private boolean isProductFailureStatusValid(Long id) {
        return productFailureWithGuaranteeExpiredReportRepository
                .findById(id).map(productFailureWithGuaranteeExpiredReport -> productFailureWithGuaranteeExpiredReport.getStatus().equals(ProductFailureReportStatus.PROPOSED))
                .orElseGet(() -> true);
    }

    private boolean isCustomerNameValid(String managerUsername) {
        return Objects.nonNull(managerUsername) && managerRepository.findByUsername(managerUsername).isPresent();
    }

    private boolean doProductFailureExist(Long id, String managerUsername) {

        return Objects.nonNull(id) && productFailureWithGuaranteeExpiredReportRepository.findByIdAndManagerUsername(id, managerUsername).isPresent();
    }

    private boolean areCostsValid(BigDecimal costs) {
        return Objects.isNull(costs) || costs.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isValidCompletionDate(LocalDate completionDate) {
        return Objects.isNull(completionDate) || completionDate.compareTo(LocalDate.now()) > 0;
    }
}
