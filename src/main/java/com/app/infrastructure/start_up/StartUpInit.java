package com.app.infrastructure.start_up;

import com.app.application.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class StartUpInit {

    private final ScheduleService scheduleService;

    @PostConstruct
    private void init() {
        scheduleService.scheduleTasks();
    }

}
