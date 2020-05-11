package com.app.infrastructure.controller;

import com.app.application.service.ComplaintService;
import com.app.infrastructure.dto.ComplaintDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.UpdateComplaintDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/complaints")
public class ComplaintController { /*USER_MANGER*/

    private final ComplaintService complaintService;

    @GetMapping
    public ResponseEntity<ResponseData<List<ComplaintDto>>> getAll(
            @AuthenticationPrincipal String username
    ) {

        var body = ResponseData.<List<ComplaintDto>>builder()
                .data(complaintService.getAllComplaintsByManagerUsername(username))
                .build();

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<ComplaintDto>> getById(
            @AuthenticationPrincipal String username,
            @PathVariable Long id) {

        var body = ResponseData.<ComplaintDto>builder()
                .data(complaintService.getComplaintByIdAndManagerUsername(id, username))
                .build();

        return ResponseEntity.ok(body);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseData<Long>> updateById(
            @AuthenticationPrincipal String username,
            @PathVariable Long id,
            RequestEntity<UpdateComplaintDto> requestEntity) {

        var body = ResponseData.<Long>builder()
                .data(complaintService.updateComplaintById(id, username, requestEntity.getBody()))
                .build();

        return ResponseEntity.ok(body);

    }
}
