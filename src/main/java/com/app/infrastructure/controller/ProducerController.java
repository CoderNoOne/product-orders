package com.app.infrastructure.controller;

import com.app.application.service.ProducerService;
import com.app.infrastructure.dto.ProducerDto;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/producers")
@RequiredArgsConstructor
public class ProducerController {

    private final ProducerService producerService;

    @GetMapping
    public ResponseEntity<ResponseData<List<ProducerDto>>> getAll(@RequestParam(name = "trade", required = false) String trade) {

        var body = ResponseData.<List<ProducerDto>>builder()
                .data(Objects.isNull(trade) ? producerService.getAllProducers() : producerService.getProducersByTrade(trade))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
