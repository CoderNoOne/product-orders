package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.enums.ComplaintStatus;
import com.app.infrastructure.dto.UpdateComplaintDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Component
@SessionScope
public class UpdateComplaintDtoValidator extends AbstractValidator<UpdateComplaintDto> {

    @Override
    public Map<String, String> validate(UpdateComplaintDto updateComplaintDto) {

        errors.clear();

        if (Objects.isNull(updateComplaintDto)) {
            errors.put("UpdateComplaintDto object", "is null");
            return errors;
        }

        if (Objects.isNull(updateComplaintDto.getStatus())) {
            errors.put("Status", "is null");
        } else if (!isStatusValid(updateComplaintDto.getStatus())){
            errors.put("Status", "is not valid");
        }
            return errors;
    }

    private boolean isStatusValid(String status) {
        return Arrays.stream(ComplaintStatus.values()).map(ComplaintStatus::name)
                .anyMatch(enumVal -> enumVal.equalsIgnoreCase(status));
    }
}
