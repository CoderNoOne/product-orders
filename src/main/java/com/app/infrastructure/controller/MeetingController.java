package com.app.infrastructure.controller;

import com.app.application.service.MeetingService;
import com.app.application.service.UserService;
import com.app.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<MeetingDto>> getMeetings(
            @RequestParam(required = false, name = "status") String status
    ) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        var isManager = userService.isManager(username);

        return ResponseData.<List<MeetingDto>>builder()
                .data(meetingService.getMeetings(status, isManager, username))
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> save(@RequestBody CreateMeetingDto createMeetingDto) {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(meetingService.save(managerUsername, createMeetingDto))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        meetingService.delete(id, managerUsername);

    }

    @GetMapping("/{id}/notices")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<NoticeDto>> getAllNotices(@PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var isManager = userService.isManager(username);

        return ResponseData.<List<NoticeDto>>builder()
                .data(meetingService.getAllNotices(id, username, isManager))
                .build();

    }

    @PostMapping("{id}/notices")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> saveNotice(@PathVariable Long id, @RequestBody CreateNoticeForMeetingDto createNoticeForMeetingDto) {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(meetingService.addNotice(id, createNoticeForMeetingDto, managerUsername))
                .build();

    }
}
