package com.app.infrastructure.controller;

import com.app.application.service.*;
import com.app.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RequestMapping("/productOrders")
@RequiredArgsConstructor
@RestController
public class ProductOrderController { /*USER_CUSTOMER*/

    private final ProductOrderService productOrderService;
    private final EmailService emailService;
    private final UserService userService;
    private final ComplaintService complaintService;


    @PostMapping
    public ResponseEntity<ResponseData<Long>> addProduct(@RequestBody CreateProductOrderDto2 createProductOrderDto) {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        var body = ResponseData.<Long>builder()
                .data(productOrderService.addProductOrder(managerUsername, createProductOrderDto))
                .build();

//        emailService.sendAsHtml(null, userService.getEmailForUsername(username), MailTemplates.generateHtmlInfoAboutProductOrder(username, productOrderService.getById(body.getData())), "Product order information");
        return new ResponseEntity<>(body, HttpStatus.CREATED);

    }


    @PatchMapping("/{id}/pay")
    public ResponseEntity<ResponseData<Long>> makePurchase(
            @PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();


        var body = ResponseData.<Long>builder()
                .data(productOrderService.makePurchaseForOrderByIdAndUsername(id, username))
                .build();

        emailService.sendAsHtml(null, userService.getEmailForUsername(username), MailTemplates.generateHtmlInfoAboutSuccessfulPayment(username, productOrderService.getById(id)), "Product payment information");

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @GetMapping("/price/groupBy/{key}")
    public ResponseEntity<ResponseData<Map<String, BigDecimal>>> getTotalPriceGroupByKey(
            @PathVariable String key) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

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
            RequestEntity<OrderDateBoundaryDto> requestEntity) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();


        var body = ResponseData.<BigDecimal>builder()
                .data(productOrderService.getTotalPriceByOrderDateForUsername(requestEntity.getBody(), username))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseData<List<ProductOrderDto>>> getFiltered(
            RequestEntity<ProductOrderFilteringCriteriaDto> requestEntity) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println(requestEntity.getBody());

        var body = ResponseData.<List<ProductOrderDto>>builder()
                .data(Objects.nonNull(requestEntity.getBody()) ?
                        productOrderService.getFilteredProductOrdersForUsername(requestEntity.getBody(), username)
                        : productOrderService.getAllProductOrdersForUsername(username)
                )
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @GetMapping("/filterByKey")
    public ResponseEntity<ResponseData<List<ProductOrderDto>>> getFilteredByKeyWord(
            @RequestParam(name = "keyWord") String keyWord) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();


        var body = ResponseData.<List<ProductOrderDto>>builder()
                .data(productOrderService.getFilteredProductOrdersByKeyWordForUsername(KeywordDto.builder().word(keyWord).build(), username))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @PostMapping("/{id}/complaints")
    public ResponseEntity<ResponseData<Long>> addComplaint(
            @PathVariable Long id,
            RequestEntity<CreateComplaintDto> requestEntity) {


        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        var body = ResponseData.<Long>builder()
                .data(productOrderService.addComplaint(id, username, requestEntity.getBody()))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Void>> cancelProductOrder(
            @PathVariable Long id
    ) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

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
            @PathVariable Long id
    ) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

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

//    @GetMapping
//    public ResponseEntity<ResponseData<List<ProductOrderDto>>> getAllProductOrders(
//    ) {
//
//        var username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//
//        var body = ResponseData.<List<ProductOrderDto>>builder()
//                .data(productOrderService.getAllProductOrdersForUsername(username))
//                .build();
//
//        return new ResponseEntity<>(body, HttpStatus.OK);
//    }

}
