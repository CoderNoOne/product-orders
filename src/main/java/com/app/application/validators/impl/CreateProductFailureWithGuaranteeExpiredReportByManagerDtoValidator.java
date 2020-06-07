package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.entity.Complaint;
import com.app.domain.entity.Product;
import com.app.domain.entity.ProductOrder;
import com.app.domain.enums.ComplaintStatus;
import com.app.domain.other.Period;
import com.app.domain.repository.ComplaintRepository;
import com.app.infrastructure.dto.CreateProductFailureWithGuaranteeExpiredReportByManagerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@SessionScope
@RequiredArgsConstructor
public class CreateProductFailureWithGuaranteeExpiredReportByManagerDtoValidator extends AbstractValidator<CreateProductFailureWithGuaranteeExpiredReportByManagerDto> {

    private final ComplaintRepository complaintRepository;

    @Override
    public Map<String, String> validate(CreateProductFailureWithGuaranteeExpiredReportByManagerDto createProductFailureWithGuaranteeExpiredReportByManagerDto) {

        if (Objects.isNull(createProductFailureWithGuaranteeExpiredReportByManagerDto)) {
            errors.put("Object dto", "is null");
            return errors;
        }

        if (Objects.isNull(createProductFailureWithGuaranteeExpiredReportByManagerDto.getManagerUsername())) {
            errors.put("Manager username", "Manager username is null");
        }

        if (Objects.isNull(createProductFailureWithGuaranteeExpiredReportByManagerDto.getComplaintId())) {
            errors.put("Complaint id", "is null");
        } else if (!doComplaintExist(createProductFailureWithGuaranteeExpiredReportByManagerDto.getComplaintId(), createProductFailureWithGuaranteeExpiredReportByManagerDto.getManagerUsername())) {
            errors.put("Complaint id", "No complaint with id: " + createProductFailureWithGuaranteeExpiredReportByManagerDto.getComplaintId() +
                    " and for manager username: " + createProductFailureWithGuaranteeExpiredReportByManagerDto.getManagerUsername());
        } else if (!isComplaintStatusValid(createProductFailureWithGuaranteeExpiredReportByManagerDto.getComplaintId(),
                createProductFailureWithGuaranteeExpiredReportByManagerDto.getManagerUsername())) {
            errors.put("Complaint status", "should be confirmed");
        }

        if (Objects.nonNull(createProductFailureWithGuaranteeExpiredReportByManagerDto.getComplaintId()) &&
                doComplaintExist(createProductFailureWithGuaranteeExpiredReportByManagerDto.getComplaintId(), createProductFailureWithGuaranteeExpiredReportByManagerDto.getManagerUsername())
                && isComplaintStatusValid(createProductFailureWithGuaranteeExpiredReportByManagerDto.getComplaintId(), createProductFailureWithGuaranteeExpiredReportByManagerDto.getManagerUsername())
                && !isGuaranteeExpired(createProductFailureWithGuaranteeExpiredReportByManagerDto.getComplaintId())
        ) {
            errors.put("Guarantee", "Product guarantee should be expired");
        }

        if (Objects.isNull(createProductFailureWithGuaranteeExpiredReportByManagerDto.getCosts())) {
            errors.put("Costs", "Costs are null");
        } else if (!areCostsValid(createProductFailureWithGuaranteeExpiredReportByManagerDto.getCosts())) {
            errors.put("Costs", "have to be greater than 0");
        }

        if (Objects.isNull(createProductFailureWithGuaranteeExpiredReportByManagerDto.getCompletionDate())) {
            errors.put("Completion date", "is null");
        } else if (!isCompletionDateValid(createProductFailureWithGuaranteeExpiredReportByManagerDto.getCompletionDate())) {
            errors.put("Completion date", "should take place in the future");
        }

        return errors;
    }

    private boolean isGuaranteeExpired(Long complaintId) {
        Optional<Complaint> optional = complaintRepository.findById(complaintId);
        if (optional.isPresent()) {
            LocalDate guaranteeExpirationDate = calculateExpirationGuaranteeDate(
                    optional.get().getProductOrder().getOrderDate(),
                    optional.get().getProductOrder().getProduct().getGuarantee().getGuaranteeTime());

            return guaranteeExpirationDate.compareTo(LocalDate.now()) < 0;
        }
        return false;
    }

    private LocalDate calculateExpirationGuaranteeDate(LocalDate orderDate, Period guaranteeTime) {

        Integer years = guaranteeTime.getYears();
        Integer months = guaranteeTime.getMonths();
        Integer days = guaranteeTime.getDays();

        return orderDate
                .plusYears(Objects.nonNull(years) ? years : 0)
                .plusMonths(Objects.nonNull(months) ? months : 0)
                .plusDays(Objects.nonNull(days) ? days : 0);

    }

    private boolean isComplaintStatusValid(Long complaintId, String managerUsername) {
        Optional<Complaint> optional = complaintRepository.findByIdAndManagerUsername(complaintId, managerUsername);
        return optional.isPresent() && Objects.equals(optional
                .get().getStatus(), ComplaintStatus.CONFIRMED);
    }

    private boolean isCompletionDateValid(LocalDate completionDate) {
        return completionDate.compareTo(LocalDate.now()) > 0;
    }

    private boolean areCostsValid(BigDecimal costs) {
        return costs.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean doComplaintExist(Long complaintId, String managerUsername) {

        return complaintRepository.findByIdAndManagerUsername(complaintId, managerUsername).isPresent();
    }
}
