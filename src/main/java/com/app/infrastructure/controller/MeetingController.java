package com.app.infrastructure.controller;

import com.app.application.service.MeetingService;
import com.app.application.service.UserService;
import com.app.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResponseData<List<MeetingDto>>> getMeetings(
            @RequestParam(required = false, name = "status") String status
    ) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        var isManager = userService.isManager(username);

        var body = ResponseData.<List<MeetingDto>>builder()
                .data(meetingService.getMeetings(status, isManager, username))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseData<Long>> save(@RequestBody CreateMeetingDto createMeetingDto) {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        var body = ResponseData.<Long>builder()
                .data(meetingService.save(managerUsername, createMeetingDto))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        meetingService.delete(id, managerUsername);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/notices")
    public ResponseEntity<ResponseData<List<NoticeDto>>> getAllNotices(@PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var isManager = userService.isManager(username);

        var body = ResponseData.<List<NoticeDto>>builder()
                .data(meetingService.getAllNotices(id, username, isManager))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);


    }

    @PostMapping("{id}/notices")
    public ResponseEntity<ResponseData<Long>> saveNotice(@PathVariable Long id, @RequestBody CreateNoticeForMeetingDto createNoticeForMeetingDto) {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        var body = ResponseData.<Long>builder()
                .data(meetingService.addNotice(id, createNoticeForMeetingDto, managerUsername))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);

    }
}
