package com.app.application.service;

import com.app.infrastructure.dto.ProductOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
/*@RequestScope? reconsider*/
@RequiredArgsConstructor
@Slf4j
@Transactional /*?*/
public class ScheduleService {

    private final TaskScheduler executor;
    private final EmailService emailService;
    private final ProductOrderService productOrderService;
    private final AdminShopPropertyService adminShopPropertyService;

    public void scheduleTasks() {
        getTriggersForNotDonePayment();
        getTriggersForSuspension();
        getTriggersForWarningsMailing();
    }


    private void getTriggersForSuspension() {

        executor.schedule(() -> {
                    log.info("Get triggers for suspension");
                    productOrderService.getProductOrdersToSuspend()
                            .forEach((user, productOrderList) -> {
                                productOrderService.changeOrderStatusToSuspend(productOrderList.stream().map(ProductOrderDto::getId).collect(Collectors.toList()));

                                emailService.sendAsHtml(null, user.getEmail(), MailTemplates.generateHtmlInfoAboutSuspension(user.getUsername(), productOrderList), "SUSPENSION");
                            });

                }
                , new CronTrigger("0 5 0 * * ?"));
    }


    private void getTriggersForWarningsMailing() {

        executor.schedule(() -> {
                    log.info("Get triggers for warning mails invoked");
                    productOrderService.getProductOrdersWithWarnedStatusGroupByUser()
                            .forEach((user, productOrderList) -> {
                                emailService.sendAsHtml(null, user.getEmail(), MailTemplates.generateHtmlInfoAboutWarning(user.getUsername(), productOrderList), "Warning");
                            });

                }
                , new CronTrigger("0 10 0 * * ?"));
    }

    private void getTriggersForNotDonePayment() {

        executor.schedule(() -> {
            log.info("Get triggers for not done payment invoked");
            productOrderService.getProductOrdersWithNoPaymentDoneGroupedByUser()
                    .forEach((user, productOrderList) -> {
                        productOrderService.changeStatusToWarnedAndMovePaymentDeadlineByDays(productOrderList.stream().map(ProductOrderDto::getId).collect(Collectors.toList()), 7);
                        BigDecimal pValue = adminShopPropertyService.getPropertyValueByName("p");
                        String mailTitle = "Warning. Dear " + user.getUsername() + ". You need to pay for your orders!";
                        emailService.sendAsHtml(null, user.getEmail(), MailTemplates.generateHtmlInfoAboutNotPaymentDone(user.getUsername(), productOrderList, pValue), mailTitle);
                    });
        }, new CronTrigger("0 0 0 * * ?"));
    }


}


