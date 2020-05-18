package com.app.application.service;

import com.app.application.validators.impl.CreateMeetingDtoValidator;
import com.app.application.validators.impl.CreateNoticeDtoValidator;
import com.app.domain.entity.Meeting;
import com.app.domain.entity.Notice;
import com.app.domain.enums.MeetingStatus;
import com.app.domain.repository.MeetingRepository;
import com.app.domain.repository.NoticeRepository;
import com.app.domain.repository.ProductOrderProposalRepository;
import com.app.infrastructure.dto.CreateMeetingDto;
import com.app.infrastructure.dto.CreateNoticeDto;
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
    private final CreateNoticeDtoValidator createNoticeDtoValidator;

    public List<MeetingDto> getMeetings(String status, boolean isManager, String username) {

        return meetingRepository.findAll()
                .stream()
                .filter(meeting -> Objects.isNull(status) || Objects.equals(meeting.getStatus().name(), status))
                .filter(meeting -> isManager ? Objects.equals(meeting.getProposalProductOrder().getCustomer().getManager().getUsername(), username) :
                        Objects.equals(meeting.getProposalProductOrder().getCustomer().getUsername(), username))
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

        if (!Objects.equals(productOrderProposal.getCustomer().getManager().getUsername(), managerUsername)) {
            throw new ValidationException("Proposal is associated with customer who is not your client!");
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

        if (!Objects.equals(meeting.getProposalProductOrder().getCustomer().getManager().getUsername(), managerUsername)) {
            throw new ValidationException("Proposal do not belong to your client");
        }

        if (meeting.getStatus().equals(MeetingStatus.FINISHED)) {
            throw new ValidationException("Meeting has been finished. Cannot be canceled");
        }

        meetingRepository.delete(meeting);
    }

    public List<NoticeDto> getAllNotices(Long id, String username, boolean isManager) {

        var meeting = meetingRepository.findOne(id).orElseThrow(() -> new NotFoundException("No meeting with id: " + id));

        var doBelongToUser = isManager ? Objects.equals(meeting.getProposalProductOrder().getCustomer().getManager().getUsername(), username) :
                Objects.equals(meeting.getProposalProductOrder().getCustomer().getUsername(), username);

        if (!doBelongToUser) {
            throw new ValidationException("You have no access to that notices");
        }

        return meeting.getNotices()
                .stream()
                .map(Notice::toDto)
                .collect(Collectors.toList());
    }

    public Long addNotice(Long id, CreateNoticeDto createNoticeDto, String managerUsername) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Meeting id is null");
        }

        var errors = createNoticeDtoValidator.validate(createNoticeDto);

        Optional<Meeting> meeting;
        if ((meeting = meetingRepository.findOne(id)).isEmpty()) {
            errors.put("Meeting id", "No meeting with id " + id);
        } else if (!Objects.equals(meeting.get().getProposalProductOrder().getCustomer().getManager().getUsername(), managerUsername)) {
            errors.put("Manager username", "That meeting is not managed by you");
        }

        if (createNoticeDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var notice = createNoticeDto.toEntity();
        var savedNotice = noticeRepository.save(notice);
        meeting.ifPresent(value -> value.getNotices().add(savedNotice));

        return savedNotice.getId();
    }
}
