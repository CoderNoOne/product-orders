package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.infrastructure.dto.CreateNoticeDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

// TODO: 18.05.2020 KM session scope
@SessionScope
@Component
public class CreateNoticeDtoValidator extends AbstractValidator<CreateNoticeDto> {

    @Override
    public Map<String, String> validate(CreateNoticeDto createNoticeDto) {

        errors.clear();

        if (Objects.isNull(createNoticeDto)) {
            errors.put("CreateNoticeDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createNoticeDto.getContent())) {
            errors.put("Content", "is null");
        } else if (!isContentValidLength(createNoticeDto.getContent())) {
            errors.put("Content", "Maximum length is 2000 characters");
        }


        if (Objects.isNull(createNoticeDto.getTittle())) {
            errors.put("Tittle", "is null");
        } else if (!isTittleValidLength(createNoticeDto.getTittle())) {
            errors.put("Tittle", "Maximum length is 50 characters");
        }


        return errors;
    }

    private boolean isTittleValidLength(String tittle) {
        return tittle.length() <= 50;
    }

    private boolean isContentValidLength(String content) {
        return content.length() <= 2000;
    }
}
