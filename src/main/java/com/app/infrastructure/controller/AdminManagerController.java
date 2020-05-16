package com.app.infrastructure.controller;

import com.app.application.service.EmailService;
import com.app.application.service.ManagerService;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/managers")
public class AdminManagerController { /*ADMIN_MANAGER*/

    private final ManagerService managerService;
    private final EmailService emailService;

    @PutMapping("/{id}/activate")
    public ResponseEntity<ResponseData<Long>> activateManagerAccount(@PathVariable Long id) {

        var body = ResponseData.<Long>builder()
                .data(managerService.activate(id))
                .build();

//        emailService.sendAsHtml();

        return new ResponseEntity<>(body, HttpStatus.OK);

    }
}
