package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.ComplaintRepository;
import com.app.infrastructure.dto.CreateRepairOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Component
@SessionScope
@RequiredArgsConstructor
public class CreateRepairOrderDtoValidator extends AbstractValidator<CreateRepairOrderDto> {

    private final ComplaintRepository complaintRepository;

    @Override
    public Map<String, String> validate(CreateRepairOrderDto createRepairOrderDto) {

        errors.clear();

        if (Objects.isNull(createRepairOrderDto)) {
            errors.put("CreateRepairOrderDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createRepairOrderDto.getComplaintId())) {
            errors.put("Complaint id", "is null");
        } else if (!doComplaintExist(createRepairOrderDto.getComplaintId())) {
            errors.put("Complaint object", "No complaint with id: " + createRepairOrderDto.getComplaintId());
        }

        if (Objects.isNull(createRepairOrderDto.getCompletionDate())) {
            errors.put("Completion date", "is null");
        } else if (!isCompletionDateValid(createRepairOrderDto.getCompletionDate())) {
            errors.put("Completion date", "is not valid");
        }

        if (Objects.isNull(createRepairOrderDto.getRepairCosts())) {
            errors.put("Repair costs", "is null");
        } else if (Objects.nonNull(createRepairOrderDto.getComplaintId()) && doComplaintExist(createRepairOrderDto.getComplaintId())) {

            if (!isGuaranteeActive(createRepairOrderDto.getComplaintId())) {
                errors.put("", "");
            }

        }
//            errors.put("Repair costs", "should  be greater than or equal to 0");


        return errors;
    }

    private boolean isGuaranteeActive(Long complaintId) {

        complaintRepository.findOne(complaintId).get().getProductOrder().getOrderDate();

        return false;
    }

    private boolean doComplaintExist(Long complaintId) {
        return complaintRepository.findOne(complaintId).isPresent();
    }

    private boolean isRepairCostsValid(BigDecimal repairCosts) {

        return repairCosts.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isCompletionDateValid(LocalDate completionDate) {
        return completionDate.compareTo(LocalDate.now()) > 0;
    }


}
