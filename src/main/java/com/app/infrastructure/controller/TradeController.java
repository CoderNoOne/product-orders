package com.app.infrastructure.controller;

import com.app.application.service.TradeService;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.dto.TradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trades")
public class TradeController {

    private final TradeService tradeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<TradeDto>> getAll() {

        return ResponseData.<List<TradeDto>>builder()
                .data(tradeService.getAllTrades())
                .build();
    }
}
