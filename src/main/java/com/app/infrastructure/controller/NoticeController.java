package com.app.infrastructure.controller;

import com.app.application.service.EmailService;
import com.app.application.service.MailTemplates;
import com.app.application.service.NoticeService;
import com.app.application.service.UserService;
import com.app.infrastructure.dto.CreateNoticeDto;
import com.app.infrastructure.dto.NoticeDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;
    private final EmailService emailService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseData<Long>> addNotice(@RequestBody CreateNoticeDto createNoticeDto) {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        var savedNotice = noticeService.save(managerUsername, createNoticeDto);

        var body = ResponseData.<Long>builder()
                .data(savedNotice.getId())
                .build();



        // TODO: 18.05.2020 send mail to manager and customer
        emailService.sendAsHtml(null, userService.getEmailForUsername(managerUsername), MailTemplates.notifyManagerAboutAddedNotice(),"");
        emailService.sendAsHtml(null, userService.getEmailForUsername(managerUsername), MailTemplates.notifyManagerAboutAddedNotice(),"");

        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }
}
