package com.app.infrastructure.controller;

import com.app.application.service.EmailService;
import com.app.application.service.MailTemplates;
import com.app.application.service.ManagerService;
import com.app.application.service.UserService;
import com.app.infrastructure.dto.ManagerDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/managers")
public class AdminManagerController { /*ADMIN_MANAGER*/

    private final ManagerService managerService;
    private final UserService userService;
    private final EmailService emailService;

    @PutMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> activateManagerAccount(@PathVariable Long id) {

        ManagerDto activatedManager = managerService.activate(id);

        var body = ResponseData.<Long>builder()
                .data(activatedManager.getId())
                .build();

        emailService.sendAsHtml(null, userService.getEmailById(body.getData()), MailTemplates.generateHtmlInfoAboutManagerAccountActivation(activatedManager), "Your account has been activated");

        return body;

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ManagerDto>> getAllManagers(@RequestParam(name = "enabled", required = false) Boolean enabled) {

        return ResponseData.<List<ManagerDto>>builder()
                .data(managerService.getAll(enabled))
                .build();
    }
}
