package com.app.infrastructure.controller;

import com.app.application.service.*;
import com.app.infrastructure.dto.CreateNoticeDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;
    private final EmailService emailService;
    private final UserService userService;
    private final ProductOrderProposalService productOrderProposalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> addNotice(@RequestBody CreateNoticeDto createNoticeDto) {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        var savedNotice = noticeService.save(managerUsername, createNoticeDto);

        var body = ResponseData.<Long>builder()
                .data(savedNotice.getId())
                .build();

        var productOrderProposal = productOrderProposalService.getById(savedNotice.getMeetingDto().getOrderProposalId());

        emailService.sendBulk(
                emailService.createMimeMessage(userService.getEmailForUsername(managerUsername), MailTemplates.notifyManagerAboutAddedNotice(managerUsername, savedNotice, productOrderProposal), "Notice added"),
                emailService.createMimeMessage(userService.getEmailForUsername(savedNotice.getMeetingDto().getCustomerDto().getEmail()), MailTemplates.notifyCustomerAboutAddedNotice(savedNotice, productOrderProposal), "Notice added")
        );

        return body;
    }
}
