package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.infrastructure.dto.CreateNoticeForMeetingDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@SessionScope
@Component
public class CreateNoticeForMeetingDtoValidator extends AbstractValidator<CreateNoticeForMeetingDto> {

    @Override
    public Map<String, String> validate(CreateNoticeForMeetingDto createNoticeForMeetingDto) {

        errors.clear();

        if (Objects.isNull(createNoticeForMeetingDto)) {
            errors.put("CreateNoticeDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createNoticeForMeetingDto.getContent())) {
            errors.put("Content", "is null");
        } else if (!isContentValidLength(createNoticeForMeetingDto.getContent())) {
            errors.put("Content", "Maximum length is 2000 characters. Minimum 10");
        }


        if (Objects.isNull(createNoticeForMeetingDto.getTittle())) {
            errors.put("Tittle", "is null");
        } else if (!isTittleValidLength(createNoticeForMeetingDto.getTittle())) {
            errors.put("Tittle", "Maximum length is 50 characters. Minimum 5");
        }

        return errors;
    }

    private boolean isTittleValidLength(String tittle) {
        return tittle.length() <= 50 && tittle.length() >= 5;
    }

    private boolean isContentValidLength(String content) {
        return content.length() <= 2000 && content.length() >= 10;
    }
}
