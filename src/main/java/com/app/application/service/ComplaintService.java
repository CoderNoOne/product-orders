package com.app.application.service;

import com.app.application.validators.impl.CreateComplaintDtoValidator;
import com.app.domain.entity.Complaint;
import com.app.domain.enums.ComplaintStatus;
import com.app.domain.enums.DamageType;
import com.app.domain.repository.ComplaintRepository;
import com.app.domain.repository.ProductOrderRepository;
import com.app.infrastructure.dto.ComplaintDto;
import com.app.infrastructure.dto.CreateComplaintDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final CreateComplaintDtoValidator createComplaintDtoValidator;
    private final ProductOrderRepository productOrderRepository;

    public ComplaintDto getComplaintByIdAndManagerUsername(Long id, String username) {

        return complaintRepository.findByIdAndManagerUsername(id, username)
                .map(Complaint::toDto)
                .orElseThrow(() -> new NotFoundException("No complaint with id: " + id + " that is managed by manager: " + username));
    }

    public List<ComplaintDto> getComplaintsByManagerUsernameAndStatus(String username, ComplaintStatus status) {

        return complaintRepository.findAllByManagerUsername(username)
                .stream()
                .filter(complaint -> !Objects.nonNull(status) || complaint.getStatus() == status)
                .map(Complaint::toDto)
                .collect(Collectors.toList());
    }

    private Long updateComplaintById(Long id, String username, ComplaintStatus status) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Complaint id is null");
        }

        var complaintId = new AtomicLong();
        complaintRepository.findByIdAndManagerUsername(id, username)
                .ifPresentOrElse(
                        complaint -> {
                            complaint.setStatus(status);
                            complaintId.set(complaint.getId());
                        }
                        , () -> {
                            throw new NotFoundException("No complaint for this manager username and with id: " + id);
                        });

        return complaintId.get();
    }

    public Long addComplaint(String username, CreateComplaintDto createComplaintDto) {

        if (Objects.nonNull(createComplaintDto)) {
            createComplaintDto.setCustomerUsername(username);
        }

        var errors = createComplaintDtoValidator.validate(createComplaintDto);

        if (createComplaintDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var idWrapper = new AtomicLong();

        productOrderRepository.findByIdAndCustomerUsername(createComplaintDto.getProductOrderId(), username)
                .ifPresentOrElse(
                        productOrder ->
                                idWrapper
                                        .set(complaintRepository.save(Complaint.builder()
                                                .damageType(DamageType.valueOf(createComplaintDto.getDamageType()))
                                                .issueDate(LocalDate.now())
                                                .productOrder(productOrder)
                                                .status(ComplaintStatus.REQUESTED)
                                                .build())
                                                .getId())

                        , () -> {
                            throw new NotFoundException("No product order with specified details");
                        }
                );

        return idWrapper.get();
    }

    public Long accept(Long id, String username) {

        return updateComplaintById(id, username, ComplaintStatus.CONFIRMED);
    }

    public Long deny(Long id, String username) {
        return updateComplaintById(id, username, ComplaintStatus.DENIED);
    }

    public List<ComplaintDto> getComplaintsByCustomerUsernameAndStatus(String username, ComplaintStatus status) {
        return complaintRepository.findAllByCustomerUsername(username)
                .stream()
                .filter(complaint -> !Objects.nonNull(status) || complaint.getStatus() == status)
                .map(Complaint::toDto)
                .collect(Collectors.toList());
    }

    public ComplaintDto getComplaintByIdAndCustomerUsername(Long id, String username) {
        return complaintRepository.findByIdAndCustomerUsername(id, username)
                .map(Complaint::toDto)
                .orElseThrow(() -> new NotFoundException("No complaint with id: " + id + " for customer username: " + username));
    }
}
