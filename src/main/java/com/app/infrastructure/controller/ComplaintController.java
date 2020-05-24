package com.app.infrastructure.controller;

import com.app.application.service.ComplaintService;
import com.app.infrastructure.dto.ComplaintDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.UpdateComplaintDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/complaints")
public class ComplaintController { /*USER_MANGER*/

    private final ComplaintService complaintService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ComplaintDto>> getAll(
    ) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<List<ComplaintDto>>builder()
                .data(complaintService.getAllComplaintsByManagerUsername(username))
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<ComplaintDto> getById(
            @PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<ComplaintDto>builder()
                .data(complaintService.getComplaintByIdAndManagerUsername(id, username))
                .build();

    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> updateById(
            @PathVariable Long id,
            RequestEntity<UpdateComplaintDto> requestEntity) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(complaintService.updateComplaintById(id, username, requestEntity.getBody()))
                .build();
    }
}
