package com.app.application.service;

import com.app.application.validators.impl.CreateProductFailureOnGuaranteeReportDtoValidator;
import com.app.domain.entity.Complaint;
import com.app.domain.entity.ProductFailureOnGuaranteeReport;
import com.app.domain.enums.ComplaintStatus;
import com.app.domain.enums.GuaranteeComponent;
import com.app.domain.other.Period;
import com.app.domain.repository.ComplaintRepository;
import com.app.domain.repository.ProductFailureOnGuaranteeReportRepository;
import com.app.infrastructure.dto.CreateProductFailureOnGuaranteeReportDto;
import com.app.infrastructure.dto.ProductFailureOnGuaranteeReportDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductFailureOnGuaranteeReportService {

    private final ProductFailureOnGuaranteeReportRepository productFailureOnGuaranteeReportRepository;
    private final ComplaintRepository complaintRepository;
    private final CreateProductFailureOnGuaranteeReportDtoValidator validator;

    public ProductFailureOnGuaranteeReportDto save(CreateProductFailureOnGuaranteeReportDto createProductFailureOnGuaranteeReportDto, String managerUsername) {

        if(Objects.nonNull(createProductFailureOnGuaranteeReportDto)){
            createProductFailureOnGuaranteeReportDto.setManagerUsername(managerUsername);
        }

        var errors = validator.validate(createProductFailureOnGuaranteeReportDto);

        if (validator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        Complaint complaint = complaintRepository.findByIdAndManagerUsernameAndStatus(createProductFailureOnGuaranteeReportDto.getComplaintId(),
                createProductFailureOnGuaranteeReportDto.getManagerUsername(), ComplaintStatus.CONFIRMED)
                .orElseThrow(() -> new NotFoundException("No confirmed complaint with id: " + createProductFailureOnGuaranteeReportDto.getComplaintId()));

        complaint.setStatus(ComplaintStatus.DONE);

        var toSave = ProductFailureOnGuaranteeReport.builder()
                .completionDate(setCompletionDate(complaint))
                .selectedServiceType(GuaranteeComponent.valueOf(createProductFailureOnGuaranteeReportDto.getSelectedServiceType().toUpperCase()))
                .damageType(complaint.getDamageType())
                .productOrder(complaint.getProductOrder())
                .build();

        return productFailureOnGuaranteeReportRepository.save(toSave).toProductFailureOnGuaranteeReportDto();

    }

    private LocalDate setCompletionDate(Complaint complaint) {

        Period guaranteeProcessingTime = complaint.getProductOrder().getProduct().getGuarantee().getGuaranteeProcessingTime();

        return LocalDate.now()
                .plusMonths(Objects.nonNull(guaranteeProcessingTime.getMonths()) ? guaranteeProcessingTime.getMonths() : 0)
                .plusDays(Objects.nonNull(guaranteeProcessingTime.getDays()) ? guaranteeProcessingTime.getDays() : 0);

    }


}
