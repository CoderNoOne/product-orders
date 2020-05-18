package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.ProductOrderProposalRepository;
import com.app.infrastructure.dto.CreateMeetingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@SessionScope
@Component
@RequiredArgsConstructor
public class CreateMeetingDtoValidator extends AbstractValidator<CreateMeetingDto> {

    private final ProductOrderProposalRepository productOrderProposalRepository;

    @Override
    public Map<String, String> validate(CreateMeetingDto createMeetingDto) {

        errors.clear();

        if (Objects.isNull(createMeetingDto)) {
            errors.put("CreateMeetingDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createMeetingDto.getMeetingDate())) {
            errors.put("Meeting date", "is null");
        } else if (!isMeetingDateValid(createMeetingDto.getMeetingDate())) {
            errors.put("Meeting date", "should take place in the future");
        }

        if (Objects.isNull(createMeetingDto.getProductOrderProposalId())) {
            errors.put("ProductProposal id", "is null");
        } else if (!doProductProposalExist(createMeetingDto.getProductOrderProposalId())) {
            errors.put("ProductProposal object", "does not exist");
        }

        return errors;
    }

    private boolean doProductProposalExist(Long productOrderProposalId) {
        return productOrderProposalRepository.findOne(productOrderProposalId).isPresent();
    }

    private boolean isMeetingDateValid(LocalDate meetingDate) {
        return meetingDate.compareTo(LocalDate.now()) > 0;
    }
}
