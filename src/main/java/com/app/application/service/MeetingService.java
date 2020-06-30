package com.app.application.service;

import com.app.application.validators.impl.CreateMeetingDtoValidator;
import com.app.application.validators.impl.CreateNoticeForMeetingDtoValidator;
import com.app.domain.entity.Meeting;
import com.app.domain.entity.Notice;
import com.app.domain.enums.MeetingStatus;
import com.app.domain.repository.MeetingRepository;
import com.app.domain.repository.NoticeRepository;
import com.app.domain.repository.ProductOrderProposalRepository;
import com.app.infrastructure.dto.CreateMeetingDto;
import com.app.infrastructure.dto.CreateNoticeForMeetingDto;
import com.app.infrastructure.dto.MeetingDto;
import com.app.infrastructure.dto.NoticeDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final ProductOrderProposalRepository productOrderProposalRepository;
    private final NoticeRepository noticeRepository;
    private final CreateMeetingDtoValidator createMeetingDtoValidator;
    private final CreateNoticeForMeetingDtoValidator createNoticeForMeetingDtoValidator;

    public List<MeetingDto> getMeetings(String status, boolean isManager, String username) {

        return meetingRepository.findAll()
                .stream()
                .filter(meeting -> Objects.isNull(status) || Objects.equals(meeting.getStatus().name(), status))
                .filter(meeting -> isManager ? meeting.getProposalProductOrder().getManager().getUsername().equals(username)
                        : meeting.getProposalProductOrder().getCustomer().getUsername().equals(username))
                .map(Meeting::toDto)
                .collect(Collectors.toList());
    }

    public Long save(String managerUsername, CreateMeetingDto createMeetingDto) {

        var errors = createMeetingDtoValidator.validate(createMeetingDto);

        if (createMeetingDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }


        var productOrderProposal = productOrderProposalRepository.findOne(createMeetingDto.getProductOrderProposalId())
                .orElseThrow(() ->
                        new NotFoundException("No productOrderProposal with id: " + createMeetingDto.getProductOrderProposalId()));

        if (!productOrderProposal.getManager().getUsername().equals(managerUsername)) {
            throw new ValidationException("You cannot add meeting regarding this productOrder proposal.");
        }

        var meeting = createMeetingDto.toEntity();

        meeting.setProductOrderProposal(productOrderProposal);

        return meetingRepository.save(meeting).getId();
    }

    public void delete(Long id, String managerUsername) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Id is null");
        }

        var meeting = meetingRepository.findOne(id).orElseThrow(() -> new NotFoundException("No meeting with id: " + id));


        if (meeting.getStatus().equals(MeetingStatus.FINISHED)) {
            throw new ValidationException("Meeting has been finished. Cannot be canceled");
        }

        if (!meeting.getProposalProductOrder().getManager().getUsername().equals(managerUsername)) {
            throw new ValidationException("You have no permission to delete this meeting");
        }

        meetingRepository.delete(meeting);
    }

    public List<NoticeDto> getAllNotices(Long id, String username, boolean isManager) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Id is null");
        }

        var meeting = meetingRepository.findOne(id).orElseThrow(() -> new NotFoundException("No meeting with id: " + id));

        if (isManager && !meeting.getProposalProductOrder().getManager().getUsername().equals(username)) {
            throw new ValidationException("No permission. You are not manager of this meeting");

        } else if (!isManager && !meeting.getProposalProductOrder().getCustomer().getUsername().equals(username)) {
            throw new ValidationException("No permission. You are not customer of this meeting");
        }


        return meeting.getNotices()
                .stream()
                .map(Notice::toDto)
                .collect(Collectors.toList());
    }

    public Long addNotice(Long id, CreateNoticeForMeetingDto createNoticeForMeetingDto, String managerUsername) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Meeting id is null");
        }

        var errors = createNoticeForMeetingDtoValidator.validate(createNoticeForMeetingDto);

        Optional<Meeting> meeting;
        if ((meeting = meetingRepository.findOne(id)).isEmpty()) {
            errors.put("Meeting id", "No meeting with id " + id);
        }
        if (createNoticeForMeetingDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        if (!(meeting.isPresent() && meeting.get().getProposalProductOrder().getManager().getUsername().equals(managerUsername))) {
            throw new ValidationException("No permission. You are not manager of this meeting");
        }

        var notice = createNoticeForMeetingDto.toEntity();
        var savedNotice = noticeRepository.save(notice);
        meeting.ifPresent(value -> value.getNotices().add(savedNotice));

        return savedNotice.getId();

    }
}
