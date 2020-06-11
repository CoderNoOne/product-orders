package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.enums.ProductFailureReportStatus;
import com.app.domain.enums.ProposalSide;
import com.app.domain.repository.CustomerRepository;
import com.app.domain.repository.ProductFailureWithGuaranteeExpiredReportRepository;
import com.app.infrastructure.dto.UpdateProductFailureWithGuaranteeExpiredReportByCustomerDto;
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
public class UpdateProductFailureWithGuaranteeExpiredReportByCustomerDtoValidator extends AbstractValidator<UpdateProductFailureWithGuaranteeExpiredReportByCustomerDto> {

    private final ProductFailureWithGuaranteeExpiredReportRepository productFailureWithGuaranteeExpiredReportRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Map<String, String> validate(UpdateProductFailureWithGuaranteeExpiredReportByCustomerDto updateProductFailureWithGuaranteeExpiredReportByCustomerDto) {

        errors.clear();

        if (Objects.isNull(updateProductFailureWithGuaranteeExpiredReportByCustomerDto)) {
            errors.put("dto object", "is null");
            return errors;
        }

        if (!isCustomerNameValid(updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getCustomerUsername())) {
            errors.put("Customer name", "doesn't exist");

        } else {

            if (!doProductFailureExist(updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getProductFailureWithGuaranteeExpiredReportId(), updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getCustomerUsername())) {
                errors.put("Product failure", "doesn't exist");
            } else {

                if (!isProductFailureStatusValid(updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getProductFailureWithGuaranteeExpiredReportId())) {
                    errors.put("Status", "should be proposed");
                }

                if (!isProductFailureSideValid(updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getProductFailureWithGuaranteeExpiredReportId())) {
                    errors.put("Side", "You cannot reply to your own proposal");
                }
            }
        }

        if (!isValidCompletionDate(updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getCompletionDate())) {
            errors.put("Completion date", "should be null or in the future");
        }
        if (!areCostsValid(updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getCosts())) {
            errors.put("Costs", "should be null or grater than 0");
        }

        return errors;

    }

    private boolean isProductFailureSideValid(Long id) {

        return productFailureWithGuaranteeExpiredReportRepository
                .findById(id).map(productFailureWithGuaranteeExpiredReport -> productFailureWithGuaranteeExpiredReport.getSide().equals(ProposalSide.MANAGER))
                .orElseGet(() -> true);
    }

    private boolean isProductFailureStatusValid(Long id) {
        return productFailureWithGuaranteeExpiredReportRepository
                .findById(id).map(productFailureWithGuaranteeExpiredReport -> productFailureWithGuaranteeExpiredReport.getStatus().equals(ProductFailureReportStatus.PROPOSED))
                .orElseGet(() -> true);
    }

    private boolean isCustomerNameValid(String customerUsername) {
        return Objects.nonNull(customerUsername) && customerRepository.findByUsername(customerUsername).isPresent();
    }

    private boolean doProductFailureExist(Long id, String customerUsername) {

        return Objects.nonNull(id) && productFailureWithGuaranteeExpiredReportRepository.findByIdAndCustomerUsername(id, customerUsername).isPresent();
    }

    private boolean areCostsValid(BigDecimal costs) {
        return Objects.isNull(costs) || costs.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isValidCompletionDate(LocalDate completionDate) {
        return Objects.isNull(completionDate) || completionDate.compareTo(LocalDate.now()) > 0;
    }
}
