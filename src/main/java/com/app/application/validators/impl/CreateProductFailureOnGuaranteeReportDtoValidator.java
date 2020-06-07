package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.enums.ComplaintStatus;
import com.app.domain.enums.GuaranteeComponent;
import com.app.domain.repository.ComplaintRepository;
import com.app.infrastructure.dto.CreateProductFailureOnGuaranteeReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Component
@SessionScope
@RequiredArgsConstructor
public class CreateProductFailureOnGuaranteeReportDtoValidator extends AbstractValidator<CreateProductFailureOnGuaranteeReportDto> {

    private final ComplaintRepository complaintRepository;

    @Override
    public Map<String, String> validate(CreateProductFailureOnGuaranteeReportDto createProductFailureOnGuaranteeReportDto) {

        errors.clear();

        if (Objects.isNull(createProductFailureOnGuaranteeReportDto)) {
            errors.put("dto object", "is null");
            return errors;
        }

        if (Objects.isNull(createProductFailureOnGuaranteeReportDto.getManagerUsername())) {
            errors.put("Manager username", "Manager username is null");
        }

        if (Objects.isNull(createProductFailureOnGuaranteeReportDto.getComplaintId())) {
            errors.put("Complaint id", "is null");
        } else if (Objects.nonNull(createProductFailureOnGuaranteeReportDto.getManagerUsername())
                && !isComplaintIdValid(createProductFailureOnGuaranteeReportDto.getComplaintId(), createProductFailureOnGuaranteeReportDto.getManagerUsername())) {
            errors.put("Complaint object", "No confirmed complaint exists");
        }

        if (Objects.isNull(createProductFailureOnGuaranteeReportDto.getSelectedServiceType())) {
            errors.put("Selected service type", "is  null");
        } else if (!isSelectedServiceValid(createProductFailureOnGuaranteeReportDto.getSelectedServiceType())) {
            errors.put("Not valid service type", "Possible service types are: EXCHANGE and REPAIR");
        }

        return errors;
    }

    private boolean isSelectedServiceValid(String selectedServiceType) {

        return Arrays.stream(GuaranteeComponent.values()).anyMatch(value -> Objects.equals(value.name(), selectedServiceType.toUpperCase()));

    }

    private boolean isComplaintIdValid(Long complaintId, String managerUsername) {
        return complaintRepository.findByIdAndManagerUsernameAndStatus(complaintId, managerUsername, ComplaintStatus.CONFIRMED).isPresent();
    }
}
