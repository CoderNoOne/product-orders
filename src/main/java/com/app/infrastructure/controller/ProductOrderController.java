package com.app.infrastructure.controller;

import com.app.application.service.*;
import com.app.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> addProduct(@RequestBody CreateProductOrderDto createProductOrderDto) {

        var managerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
//        emailService.sendAsHtml(null, userService.getEmailForUsername(username), MailTemplates.generateHtmlInfoAboutProductOrder(username, productOrderService.getById(body.getData())), "Product order information");

        return ResponseData.<Long>builder()
                .data(productOrderService.addProductOrder(managerUsername, createProductOrderDto))
                .build();

    }


    @PatchMapping("/{id}/pay")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Long> makePurchase(
            @PathVariable Long id) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();


        var body = ResponseData.<Long>builder()
                .data(productOrderService.makePurchaseForOrderByIdAndUsername(id, username))
                .build();

        emailService.sendAsHtml(null, userService.getEmailForUsername(username), MailTemplates.generateHtmlInfoAboutSuccessfulPayment(username, productOrderService.getById(id)), "Product payment information");

        return body;

    }

    @GetMapping("/price/groupBy/{key}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Map<String, BigDecimal>> getTotalPriceGroupByKey(
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

        return body;
    }

    @GetMapping("/totalPrice/date")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<BigDecimal> getTotalPriceByOrderDate(
            RequestEntity<OrderDateBoundaryDto> requestEntity) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();


        var body = ResponseData.<BigDecimal>builder()
                .data(productOrderService.getTotalPriceByOrderDateForUsername(requestEntity.getBody(), username))
                .build();

        return body;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ProductOrderDto>> getFiltered(
            RequestEntity<ProductOrderFilteringCriteriaDto> requestEntity) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        var body = ResponseData.<List<ProductOrderDto>>builder()
                .data(Objects.nonNull(requestEntity.getBody()) ?
                        productOrderService.getFilteredProductOrdersForUsername(requestEntity.getBody(), username)
                        : productOrderService.getAllProductOrdersForUsername(username)
                )
                .build();

        return body;

    }

    @GetMapping("/filterByKey")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<ProductOrderDto>> getFilteredByKeyWord(
            @RequestParam(name = "keyWord") String keyWord) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();


        return ResponseData.<List<ProductOrderDto>>builder()
                .data(productOrderService.getFilteredProductOrdersByKeyWordForUsername(KeywordDto.builder().word(keyWord).build(), username))
                .build();

    }

    @PostMapping("/{id}/complaints")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> addComplaint(
            @PathVariable Long id,
            RequestEntity<CreateComplaintDto> requestEntity) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(productOrderService.addComplaint(id, username, requestEntity.getBody()))
                .build();

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelProductOrder(
            @PathVariable Long id
    ) {

        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        var productOrder = productOrderService.getById(id);
        productOrderService.deleteById(id, username);

        emailService.sendAsHtml(
                null,
                userService.getEmailForUsername(username),
                MailTemplates.generateHtmlInfoAboutCancelingProductOrder(username, productOrder),
                "Product order canceled ");
    }

    @PostMapping("/{id}/invoice")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> issueAnInvoice(
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

        return body;
    }
}
