package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.MeetingRepository;
import com.app.infrastructure.dto.CreateNoticeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@SessionScope
@RequiredArgsConstructor
@Component
public class CreateNoticeDtoValidator extends AbstractValidator<CreateNoticeDto> {

    @Override
    public Map<String, String> validate(CreateNoticeDto createNoticeDto) {

        errors.clear();

        if (Objects.isNull(createNoticeDto)) {
            errors.put("CreateNoticeDto object", "is null");
            return errors;
        }

        if (Objects.isNull(createNoticeDto.getTittle())) {
            errors.put("Tittle", "is null");
        } else if (!isTittleValidLength(createNoticeDto.getTittle())) {
            errors.put("Tittle", "Tittle length should be in the range 5-50 characters");
        }

        if (Objects.isNull(createNoticeDto.getContent())) {
            errors.put("Content", "is null");
        } else if (!isContentValidLength(createNoticeDto.getContent())) {
            errors.put("Content", "Content length should be in the range 10-2000");
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
