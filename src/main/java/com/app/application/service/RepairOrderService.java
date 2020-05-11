package com.app.application.service;

import com.app.application.validators.impl.CreateRepairOrderDtoValidator;
import com.app.domain.repository.ComplaintRepository;
import com.app.domain.entity.RepairOrder;
import com.app.domain.repository.RepairOrderRepository;
import com.app.domain.enums.ComplaintStatus;
import com.app.infrastructure.dto.CreateRepairOrderDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Transactional
public class RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;
    private final ComplaintRepository complaintRepository;
    private final CreateRepairOrderDtoValidator createRepairOrderDtoValidator;

    public Long addRepairOrder(String username, CreateRepairOrderDto createRepairOrderDto) {

        var errors = createRepairOrderDtoValidator.validate(createRepairOrderDto);

        if (createRepairOrderDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var repairOrderId = new AtomicLong();
        complaintRepository.findByIdAndManagerUsername(createRepairOrderDto.getComplaintId(), username)
                .ifPresentOrElse(
                        complaint -> {

                            if (!complaint.getStatus().equals(ComplaintStatus.AWAITING)) {
                                throw new ValidationException("Complaint status should be: AWAITING");
                            }

                            repairOrderId.set(repairOrderRepository.save(RepairOrder.builder()
                                    .completionDate(createRepairOrderDto.getCompletionDate())
                                    .repairCosts(createRepairOrderDto.getRepairCosts())
                                    .productOrder(complaint.getProductOrder())
                                    .build()).getId());

                            complaint.setStatus(ComplaintStatus.CONFIRMED);
                        },
                        () -> {
                            throw new NotFoundException("No complaint with id: " + createRepairOrderDto.getComplaintId());
                        }
                );

        return repairOrderId.get();
    }

}
