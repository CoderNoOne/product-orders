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
import com.app.infrastructure.dto.UpdateProductFailureWithGuaranteeExpiredReportByCustomerDto;
import com.app.infrastructure.dto.UpdateProductFailureWithGuaranteeExpiredReportByManagerDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
import com.app.infrastructure.exception.ObjectMapperException;
import com.app.infrastructure.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.TypeFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductFailureWithGuaranteeExpiredReportService {

    private final ProductFailureWithGuaranteeExpiredReportRepository productFailureWithGuaranteeExpiredReportRepository;
    private final CreateProductFailureWithGuaranteeExpiredReportByManagerDtoValidator validator;
    private final ComplaintRepository complaintRepository;
    private final ObjectMapper mapper;


    public Long save(CreateProductFailureWithGuaranteeExpiredReportByManagerDto createProductFailureWithGuaranteeExpiredReportByManagerDto, String managerUsername) {

        if (Objects.nonNull(createProductFailureWithGuaranteeExpiredReportByManagerDto)) {
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

    public Long replyToByManager(UpdateProductFailureWithGuaranteeExpiredReportByManagerDto updateProductFailureWithGuaranteeExpiredReportByManagerDto) {

        //walidacja


        var productFailureReport = productFailureWithGuaranteeExpiredReportRepository.findByIdAndManagerUsername(updateProductFailureWithGuaranteeExpiredReportByManagerDto.getProductFailureWithGuaranteeExpiredReportId(), updateProductFailureWithGuaranteeExpiredReportByManagerDto.getManagerUsername())
                .orElseThrow(() -> new NotFoundException("No productFailureReport found"));

        return productFailureReport
                .side(ProposalSide.MANAGER)
                .costs(Objects.nonNull(updateProductFailureWithGuaranteeExpiredReportByManagerDto.getCosts()) ? updateProductFailureWithGuaranteeExpiredReportByManagerDto.getCosts() : productFailureReport.getCosts())
                .completionDate(Objects.nonNull(updateProductFailureWithGuaranteeExpiredReportByManagerDto.getCompletionDate()) ? updateProductFailureWithGuaranteeExpiredReportByManagerDto.getCompletionDate() : productFailureReport.getCompletionDate())
                .getId();
    }

    public Long replyToByCustomer(UpdateProductFailureWithGuaranteeExpiredReportByCustomerDto updateProductFailureWithGuaranteeExpiredReportByCustomerDto) {

        //walidacja

        var productFailureReport = productFailureWithGuaranteeExpiredReportRepository.findByIdAndCustomerUsername(updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getProductFailureWithGuaranteeExpiredReportId(), updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getCustomerUsername())
                .orElseThrow(() -> new NotFoundException("No productFailureReport found"));

        return productFailureReport
                .side(ProposalSide.MANAGER)
                .costs(Objects.nonNull(updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getCosts()) ? updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getCosts() : productFailureReport.getCosts())
                .completionDate(Objects.nonNull(updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getCompletionDate()) ? updateProductFailureWithGuaranteeExpiredReportByCustomerDto.getCompletionDate() : productFailureReport.getCompletionDate())
                .getId();

    }

    public Long acceptByManager(Long id, String username) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("ID is null");
        }

        var productFailureReport = productFailureWithGuaranteeExpiredReportRepository.findByIdAndManagerUsername(id, username)
                .orElseThrow(() -> new NotFoundException("No productFailureReport found"));

        if (productFailureReport.getSide().equals(ProposalSide.MANAGER)) {
            throw new ValidationException("Cannot accept your own proposal");
        }

        if (!isReadyToAccept(productFailureReport)) {
            throw new ValidationException("Cannot be accepted. Some information is missing");
        }

        return productFailureReport
                .status(ProductFailureReportStatus.ACCEPTED)
                .side(ProposalSide.MANAGER)
                .getId();
    }

    public Long acceptByCustomer(Long id, String username) {
        if (Objects.isNull(id)) {
            throw new NullIdValueException("ID is null");
        }

        var productFailureReport = productFailureWithGuaranteeExpiredReportRepository.findByIdAndCustomerUsername(id, username)
                .orElseThrow(() -> new NotFoundException("No productFailureReport found"));

        if (productFailureReport.getSide().equals(ProposalSide.CUSTOMER)) {
            throw new ValidationException("Cannot accept your own proposal");
        }

        if (!isReadyToAccept(productFailureReport)) {
            throw new ValidationException("Cannot be accepted. Some information is missing");
        }

        return productFailureReport
                .status(ProductFailureReportStatus.ACCEPTED)
                .side(ProposalSide.CUSTOMER)
                .getId();
    }

    private boolean isReadyToAccept(ProductFailureWithGuaranteeExpiredReport productFailureReport) {

        try {
            return mapper.readValue(productFailureReport.toProductFailureWithGuaranteeExpiredReportDto().toString(), new TypeReference<Map<String, Object>>() {
            })
                    .entrySet()
                    .stream()
                    .allMatch(e -> Objects.nonNull(e.getValue()));

        } catch (JsonProcessingException e) {
            log.error(Arrays.toString(e.getStackTrace()));
            throw new ObjectMapperException("Read value from object exception");
        }
    }

}
