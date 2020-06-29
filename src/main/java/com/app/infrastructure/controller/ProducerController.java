package com.app.infrastructure.controller;

import com.app.application.service.ProducerService;
import com.app.infrastructure.dto.ProducerDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/producers")
@RequiredArgsConstructor
public class ProducerController {

    private final ProducerService producerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ProducerDto>> getAll(@RequestParam(name = "trade", required = false) String trade) {

        return ResponseData.<List<ProducerDto>>builder()
                .data(Objects.isNull(trade) ? producerService.getAllProducers() : producerService.getProducersByTrade(trade))
                .build();
    }

}
