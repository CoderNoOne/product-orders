package com.app.infrastructure.controller;

import com.app.application.service.*;
import com.app.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@RequestMapping("/productOrders")
@RequiredArgsConstructor
@RestController
public class ProductOrderController { /*USER_CUSTOMER*/

    private final ProductOrderService productOrderService;
    private final EmailService emailService;
    private final UserService userService;
    private final ComplaintService complaintService;

    @PostMapping(
            consumes = {"application/xml", "application/json"})
    public ResponseEntity<ResponseData<Long>> add(
            @AuthenticationPrincipal String username,
            RequestEntity<CreateProductOrderDto> requestEntity) {


        var body = ResponseData.<Long>builder()
                .data(productOrderService.addProductOrder(username, requestEntity.getBody()))
                .build();

        emailService.sendAsHtml(null, userService.getEmailForUsername(username), MailTemplates.generateHtmlInfoAboutProductOrder(username, productOrderService.getById(body.getData())), "Product order information");
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<ResponseData<Long>> makePurchase(
            @AuthenticationPrincipal String username,
            @PathVariable Long id) {

        var body = ResponseData.<Long>builder()
                .data(productOrderService.makePurchaseForOrderByIdAndUsername(id, username))
                .build();

        emailService.sendAsHtml(null, userService.getEmailForUsername(username), MailTemplates.generateHtmlInfoAboutSuccessfulPayment(username, productOrderService.getById(id)), "Product payment information");

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @GetMapping("/price/groupBy/{key}")
    public ResponseEntity<ResponseData<Map<String, BigDecimal>>> getTotalPriceGroupByKey(
            @AuthenticationPrincipal String username,
            @PathVariable String key) {

        var body = ResponseData.<Map<String, BigDecimal>>builder()
                .data(productOrderService.groupPriceByKey(key, username))
                .build();

        emailService.sendAsHtml(
                null,
                userService.getEmailForUsername(username),
                MailTemplates.generateHtmlInfoAboutProductOrderHistory(username, body.getData(), key),
                ""
        );

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/totalPrice/date")
    public ResponseEntity<ResponseData<BigDecimal>> getTotalPriceByOrderDate(
            @AuthenticationPrincipal String username,
            RequestEntity<OrderDateBoundaryDto> requestEntity) {

        var body = ResponseData.<BigDecimal>builder()
                .data(productOrderService.getTotalPriceByOrderDateForUsername(requestEntity.getBody(), username))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping(headers = "GENERAL")
    public ResponseEntity<ResponseData<List<ProductOrderDto>>> getFiltered(
            @AuthenticationPrincipal String username,
            RequestEntity<ProductOrderFilteringCriteriaDto> requestEntity) {


        var body = ResponseData.<List<ProductOrderDto>>builder()
                .data(productOrderService.getFilteredProductOrdersForUsername(requestEntity.getBody(), username))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @GetMapping(headers = "KEYWORD")
    public ResponseEntity<ResponseData<List<ProductOrderDto>>> getFilteredByKeyWord(
            @AuthenticationPrincipal String username,
            RequestEntity<KeywordDto> requestEntity) {

        var body = ResponseData.<List<ProductOrderDto>>builder()
                .data(productOrderService.getFilteredProductOrdersByKeyWordForUsername(requestEntity.getBody(), username))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @PostMapping("/{id}/complaints")
    public ResponseEntity<ResponseData<Long>> addComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal String username,
            RequestEntity<CreateComplaintDto> requestEntity) {

        var body = ResponseData.<Long>builder()
                .data(productOrderService.addComplaint(id, username, requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);

    }

//    post /id/meeting

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Void>> cancelProductOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal String username
    ) {

        productOrderService.deleteById(id, username);

        emailService.sendAsHtml(
                null,
                userService.getEmailForUsername(username),
                MailTemplates.generateHtmlInfoAboutCancelingProductOrder(username, productOrderService.getById(id)),
                "Product order canceled ");

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/invoice")
    public ResponseEntity<ResponseData<Long>> issueAnInvoice(
            @PathVariable Long id,
            @AuthenticationPrincipal String username
    ) {

        var body = ResponseData.<Long>builder()
                .data(productOrderService.issueAnInvoice(id, username))
                .build();

        emailService.sendAsHtml(
                null,
                userService.getEmailForUsername(username),
                MailTemplates.generateHtmlInfoAboutComplaint(username, complaintService.getComplaintByIdAndManagerUsername(body.getData(), username)),
                "Invoice done");

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseData<List<ProductOrderDto>>> getAllProductOrders(
            @AuthenticationPrincipal String username) {

        var body = ResponseData.<List<ProductOrderDto>>builder()
                .data(productOrderService.getAllProductOrdersForUsername(username))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
