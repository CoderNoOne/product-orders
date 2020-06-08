package com.app.infrastructure.controller;

import com.app.application.service.ComplaintService;
import com.app.application.service.UserService;
import com.app.domain.enums.ComplaintStatus;
import com.app.infrastructure.dto.ComplaintDto;
import com.app.infrastructure.dto.CreateComplaintDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ComplaintDto>> getAll(
            @RequestParam(name = "status", required = false) ComplaintStatus status
    ) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<List<ComplaintDto>>builder()
                .data(userService.isManager(username) ?
                        complaintService.getComplaintsByManagerUsernameAndStatus(username, status) :
                        complaintService.getComplaintsByCustomerUsernameAndStatus(username, status)
                )
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<ComplaintDto> getById(@PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<ComplaintDto>builder()
                .data(userService.isManager(username) ?
                        complaintService.getComplaintByIdAndManagerUsername(id, username)
                        : complaintService.getComplaintByIdAndCustomerUsername(id, username))
                .build();

    }

    @PatchMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> accept(
            @PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(complaintService.accept(id, username))
                .build();
    }

    @PatchMapping("/{id}/deny")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> deny(
            @PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(complaintService.deny(id, username))
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> addComplaint(@RequestBody CreateComplaintDto createComplaintDto) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(complaintService.addComplaint(username, createComplaintDto))
                .build();
    }
}
