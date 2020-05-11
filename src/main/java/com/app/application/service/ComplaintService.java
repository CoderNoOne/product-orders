package com.app.application.service;

import com.app.application.validators.impl.UpdateComplaintDtoValidator;
import com.app.domain.entity.Complaint;
import com.app.domain.repository.ComplaintRepository;
import com.app.domain.enums.ComplaintStatus;
import com.app.infrastructure.dto.ComplaintDto;
import com.app.infrastructure.dto.UpdateComplaintDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UpdateComplaintDtoValidator updateComplaintDtoValidator;

    public ComplaintDto getComplaintByIdAndManagerUsername(Long id, String username) {

        return complaintRepository.findByIdAndManagerUsername(id, username)
                .map(Complaint::toDto)
                .orElseThrow(() -> new NotFoundException("No complaint with id: " + id + " that is managed by manager: " + username));
    }

    public List<ComplaintDto> getAllComplaintsByManagerUsername(String username) {

        return complaintRepository.findAllByManagerUsername(username)
                .stream()
                .map(Complaint::toDto)
                .collect(Collectors.toList());
    }

    public Long updateComplaintById(Long id, String username, UpdateComplaintDto updateComplaintDto) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Complaint id is null");
        }

        var errors = updateComplaintDtoValidator.validate(updateComplaintDto);

        if (updateComplaintDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var complaintId = new AtomicLong();
        complaintRepository.findByIdAndManagerUsername(id, username)
                .ifPresentOrElse(
                        complaint -> {
                            complaint.setStatus(ComplaintStatus.valueOf(updateComplaintDto.getStatus().toUpperCase()));
                            complaintId.set(complaint.getId());
                        }
                        , () -> {
                            throw new NotFoundException("No complaint for this manager username and with id: " + id);
                        });

        return complaintId.get();
    }
}
