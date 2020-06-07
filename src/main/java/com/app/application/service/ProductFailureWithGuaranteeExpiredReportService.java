package com.app.application.service;

import com.app.application.validators.impl.CreateProductFailureWithGuaranteeExpiredReportByManagerDtoValidator;
import com.app.domain.entity.Complaint;
import com.app.domain.entity.ProductFailureWithGuaranteeExpiredReport;
import com.app.domain.enums.ComplaintStatus;
import com.app.domain.enums.ProductFailureReportStatus;
import com.app.domain.enums.ProposalSide;
import com.app.domain.repository.ComplaintRepository;
import com.app.domain.repository.ProductFailureWithGuaranteeExpiredReportRepository;
import com.app.infrastructure.dto.CreateProductFailureWithGuaranteeExpiredReportByManagerDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductFailureWithGuaranteeExpiredReportService {

    private final ProductFailureWithGuaranteeExpiredReportRepository productFailureWithGuaranteeExpiredReportRepository;
    private final CreateProductFailureWithGuaranteeExpiredReportByManagerDtoValidator validator;
    private final ComplaintRepository complaintRepository;


    public Long save(CreateProductFailureWithGuaranteeExpiredReportByManagerDto createProductFailureWithGuaranteeExpiredReportByManagerDto, String managerUsername) {

        if(Objects.nonNull(createProductFailureWithGuaranteeExpiredReportByManagerDto)){
            createProductFailureWithGuaranteeExpiredReportByManagerDto.setManagerUsername(managerUsername);
        }

        var errors = validator.validate(createProductFailureWithGuaranteeExpiredReportByManagerDto);

        if (validator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        Complaint complaint = complaintRepository.findByIdAndManagerUsername(createProductFailureWithGuaranteeExpiredReportByManagerDto.getComplaintId(),
                createProductFailureWithGuaranteeExpiredReportByManagerDto.getManagerUsername())
                .orElseThrow(() -> new NotFoundException("No complaint with id: " + createProductFailureWithGuaranteeExpiredReportByManagerDto.getComplaintId() +
                        " and manager username " + createProductFailureWithGuaranteeExpiredReportByManagerDto.getManagerUsername()));

        ProductFailureWithGuaranteeExpiredReport toSave = ProductFailureWithGuaranteeExpiredReport.builder()
                .repairCosts(createProductFailureWithGuaranteeExpiredReportByManagerDto.getCosts())
                .completionDate(createProductFailureWithGuaranteeExpiredReportByManagerDto.getCompletionDate())
                .status(ProductFailureReportStatus.PROPOSED)
                .damageType(complaint.getDamageType())
                .productOrder(complaint.getProductOrder())
                .side(ProposalSide.MANAGER)
                .build();

        complaint.setStatus(ComplaintStatus.DONE);
        return productFailureWithGuaranteeExpiredReportRepository.save(toSave).getId();
    }
}
