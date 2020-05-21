package com.app.application.service;

import com.app.application.validators.impl.CreateNoticeDtoValidator;
import com.app.domain.repository.MeetingRepository;
import com.app.domain.repository.NoticeRepository;
import com.app.infrastructure.dto.CreateNoticeDto;
import com.app.infrastructure.dto.NoticeDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final CreateNoticeDtoValidator createNoticeDtoValidator;
    private final MeetingRepository meetingRepository;

    public NoticeDto save(String managerUsername, CreateNoticeDto createNoticeDto) {

        var errors = createNoticeDtoValidator.validate(createNoticeDto);

        if (createNoticeDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var meeting = meetingRepository.findOne(createNoticeDto.getMeetingId())
                .orElseThrow(() -> new NotFoundException("No meeting with id: " + createNoticeDto.getMeetingId()));

//        if (!Objects.equals(meeting.getProposalProductOrder().getCustomer().getManager().getUsername(), managerUsername)) {
//            throw new ValidationException("Meeting with id: " + createNoticeDto.getMeetingId() + " is not managed by you");
//        }

        var notice = createNoticeDto.toEntity();
        notice.setMeeting(meeting);

        var savedNotice = noticeRepository.save(notice);
        meeting.getNotices().add(savedNotice);

        return savedNotice.toDto();
    }
}
