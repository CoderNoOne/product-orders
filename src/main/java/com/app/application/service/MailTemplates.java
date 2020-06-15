package com.app.application.service;

import com.app.infrastructure.dto.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public interface MailTemplates {

    static String generateHtmlInfoAboutSuccessfulPayment(String username, ProductOrderDto productOrderDto) {

        BigDecimal totalPrice = productOrderDto.getProductDto().getPrice().multiply(BigDecimal.valueOf(productOrderDto.getQuantity()))
                .multiply(BigDecimal.valueOf(1).subtract(productOrderDto.getDiscount().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));

        return
                document(html(
                        body(
                                div(
                                        h1("Hello, " + username + ". You have paid for a product order").withStyle("text-align: center; color: red")
                                ),
                                div(
                                        h2("Product details are as follows: "),
                                        table(
                                                thead(
                                                        th("Product"),
                                                        th("Producer"),
                                                        th("Product quantity"),
                                                        th("Total price"),
                                                        th("Payment deadline")
                                                ),
                                                tbody(
                                                        tr(
                                                                th(productOrderDto.getProductDto().getName()),
                                                                th(productOrderDto.getProductDto().getProducer().getName()),
                                                                th(productOrderDto.getQuantity().toString()),
                                                                th(totalPrice.toString()),
                                                                th(productOrderDto.getPaymentDeadline().toString())
                                                        ).withStyle("text-align: center"))
                                        )
                                ).withStyle("width: 100%; border: 1px solid green; background-color: white; margin-left:auto;margin-right:auto;")
                        )


                ));
    }


    static String generateHtmlInfoAboutNotPaymentDone(String username, List<ProductOrderDto> productOrderDtoList, BigDecimal pValue) {

        var counter = new AtomicInteger(1);

        var totalPriceByOrder = productOrderDtoList.stream().distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        productOrderDto -> productOrderDto.getProductDto().getPrice().multiply(BigDecimal.valueOf(productOrderDto.getQuantity()))
                                .multiply(BigDecimal.valueOf(1).subtract(productOrderDto.getDiscount().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP))
                                )));

        return document(html(
                head(),
                body(
                        div(
                                h1("Hello, " + username + ". Payment deadline has been exceeded!").withStyle("text-align: center; color: red")

                        ),
                        table(thead(
                                th("Order Lp"),
                                th("Product"),
                                th("Producer"),
                                th("Product quantity"),
                                th("Total price"),
                                th("New payment deadline")
                                ),
                                tbody(
                                        each(productOrderDtoList, productOrderDto ->
                                                tr(
                                                        td(String.valueOf(counter.getAndIncrement())),
                                                        td(productOrderDto.getProductDto().getName()).withStyle("border: 1 px solid red"),
                                                        td(productOrderDto.getProductDto().getProducer().getName()),
                                                        td(productOrderDto.getQuantity().toString()),
                                                        td(totalPriceByOrder.get(productOrderDto).toString()),
                                                        td(productOrderDto.getPaymentDeadline().toString())
                                                ).withStyle("text-align: center"))
                                )).withStyle("width: 100%; border: 1px solid green; background-color: white; margin-left:auto;margin-right:auto;"),

                        div(
                                h1("The total price for each order WILL BE INCREASED by " + pValue.toString() + "% per day of delay").withStyle("text-align: center")
                        ),
                        div(
                                h2("If you won't pay for your orders, there will be consequences as follows: ").withStyle("text-align: center"),
                                ol(
                                        li("You will be entered in the list of debtors").withStyle("color: red"),
                                        li("No further orders will be possible until you settle the payments").withStyle("color: red")
                                ).withStyle("text-align: center")
                        )
                )
        ));

    }

    static String generateHtmlInfoAboutWarning(String username, List<ProductOrderDto> productOrderList) {


        return document(
                html(
                        body(

                        )
                ));
    }

    static String generateHtmlInfoAboutSuspension(String username, List<ProductOrderDto> productOrderList) {

        return document(
                html(
                        body()
                )
        );
    }

    static String generateHtmlInfoAboutSendingComplaint(String username, ComplaintDto complaintDto) {

        ProductOrderDto productOrderDto = complaintDto.getProductOrderDto();

        BigDecimal totalPrice = productOrderDto.getProductDto().getPrice().multiply(BigDecimal.valueOf(productOrderDto.getQuantity()))
                .multiply(BigDecimal.valueOf(1).subtract(productOrderDto.getDiscount().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));

        return document(
                body(
                        h1("Hello, " + username + ". Product order complaint has been send").withStyle("text-align: center; color: red"),
                        div(
                                h2("Complaint details are as follows: "),
                                table(
                                        thead(
                                                th("Product name"),
                                                th("Producer name"),
                                                th("Product quantity"),
                                                th("Total price"),
                                                th("Issue date"),
                                                th("Reported damage type")
                                        ),
                                        tbody(
                                                tr(
                                                        td(productOrderDto.getProductDto().getName()),
                                                        td(productOrderDto.getProductDto().getProducer().getName()),
                                                        td(productOrderDto.getQuantity().toString()),
                                                        td(totalPrice.toString()),
                                                        td(complaintDto.getIssueDate().toString()),
                                                        td(complaintDto.getDamageType().name())
                                                ).withStyle("text-align: center; color: #597C56")
                                        ).withStyle("width: 100%; border: 1px solid black; background-color: white; margin-left: auto; margin-right: auto;")
                                )
                        )
                )
        );
    }

    static String generateHtmlInfoAboutCancelingProductOrder(String username, ProductOrderDto productOrderDto) {

        BigDecimal totalPrice = productOrderDto.getProductDto().getPrice().multiply(BigDecimal.valueOf(productOrderDto.getQuantity()))
                .multiply(BigDecimal.valueOf(1).subtract(productOrderDto.getDiscount().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));

        return document(
                html(
                        body(
                                h1("Hello, " + username + ". Product order has been canceled").withStyle("text-align: center; color: red"),
                                div(
                                        h2("Canceled product details are as follows: "),
                                        table(
                                                thead(
                                                        th("Product"),
                                                        th("Producer"),
                                                        th("Product quantity"),
                                                        th("Total price")
                                                ),
                                                tbody(
                                                        tr(
                                                                td(productOrderDto.getProductDto().getName()),
                                                                td(productOrderDto.getProductDto().getProducer().getName()),
                                                                td(productOrderDto.getQuantity().toString()),
                                                                td(totalPrice.toString())
                                                        ).withStyle("text-align: center; color: #597C56")
                                                )
                                        ).withStyle("width: 100%; border: 1px solid black; background-color: white; margin-left: auto; margin-right: auto;")
                                )
                        )
                ));
    }

    static String generateHtmlInfoAboutProductOrder(ProductOrderDto productOrderDto) {
        BigDecimal totalPrice = productOrderDto.getProductDto().getPrice().multiply(BigDecimal.valueOf(productOrderDto.getQuantity()))
                .multiply(BigDecimal.valueOf(1).subtract(productOrderDto.getDiscount().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));
        return
                document(html(
                        head(),
                        body(
                                h1("Hello, " + productOrderDto.getCustomerDto().getUsername() + ". Product has been ordered").withStyle("text-align: center; color: red"),
                                div(
                                        h2("Product details are as follows").withStyle("margin-top: 10vmin; text-align: center; color: black"),
                                        table(thead(
                                                th("Product"),
                                                th("Producer"),
                                                th("Product quantity"),
                                                th("Total price"),
                                                th("Payment deadline ")
                                                ),
                                                tbody(
                                                        tr(
                                                                td(productOrderDto.getProductDto().getName()).withStyle("border: 1 px solid red"),
                                                                td(productOrderDto.getProductDto().getProducer().getName()),
                                                                td(productOrderDto.getQuantity().toString()),
                                                                td(totalPrice.toString()),
                                                                td(productOrderDto.getPaymentDeadline().toString())
                                                        ).withStyle("text-align: center; color: #597C56")
                                                )
                                        ).withStyle("width: 100%; border: 1px solid green; background-color: white; margin-left:auto;margin-right:auto;")).withStyle("background-color: #965D51"),
                                h3("Remember to pay before payment deadline!").withStyle("margin-top: 5vmin; text-align: center; background-color: white")
                        )));
    }

    static String generateHtmlInfoAboutProductOrderHistory(String username, Map<String, BigDecimal> data, String groupBy) {

        return document(html(
                body(
                        h1("Hello, " + username + ". Your product order history grouped by: " + groupBy).withStyle("text-align: center; color: red"),
                        div(
                                h2("Product order history"),
                                table(thead(
                                        th("Key name"),
                                        th("Sum price")
                                        ),
                                        tbody(
                                                each(data, (key, value) ->
                                                        tr(
                                                                td(key),
                                                                td(value.toString())

                                                        ).withStyle("text-align: center; color: #597C56")
                                                )
                                        )

                                ).withStyle("width: 100%; border: 1px solid green; background-color: white; margin-left:auto;margin-right:auto;")
                        )
                )
                )
        );
    }

    static String generateHtmlInfoAboutRegisteringManager(String username) {
        return document(
                html(
                        body(
                                h1("Hello, " + username).withStyle("background-color: yellow; text-align: center"),
                                h2("You have been registered. Your account will be soon activated by admin").withStyle("text-align: center"),

                                p("See you soon ;)").withStyle("text-align: center")

                        )
                )
        );
    }

    static String notifyManagerAboutAddedNotice(String managerUsername, NoticeDto savedNotice, ProductOrderProposalDto productOrderProposal) {


        return document(html(
                body(

                        h1("Hello manager - " + managerUsername).withStyle("text-align: center; background-color: yellow"),
                        h2("A notice has been added to meeting on day: " + savedNotice.getMeetingDto().getMeetingDate() + " with customer: " + savedNotice.getMeetingDto().getCustomerDto().getUsername())
                                .withStyle("text-align: center"),

                        h2("Tittle: " + savedNotice.getTittle()).withStyle("text-align: center"),
                        p(savedNotice.getContent()).withStyle("text-align: center"),

                        h2("Product order details").withStyle("text-align: center"),
                        table(
                                thead(
                                        th("Product"),
                                        th("Quantity"),
                                        th("Shop"),
                                        th("Discount"),
                                        th("Delivery address")
                                ).withStyle("text-align: center; border: solid 1px green"),
                                tbody(
                                        tr(
                                                td(productOrderProposal.getProductInfo().getName() + ":" + productOrderProposal.getProductInfo().getProducerName()),
                                                td(productOrderProposal.getQuantity().toString()),
                                                td(productOrderProposal.getShopName()),
                                                td(productOrderProposal.getDiscount().toString()),
                                                td(productOrderProposal.getAddress())
                                        )
                                ).withStyle("text-align: center; border: solid 1px green")
                        ).withStyle("width: 100%; background-color: white; margin-left:auto;margin-right:auto; border-collapse: collapse;"),
                        h2("Remarks").withStyle("text-align: center; margin-top: 5vmin; margin-bottom: 5vmin"),
                        table(thead(
                                th("Tittle"),
                                th("Content")
                                ).withStyle("text-align: center; border: solid 1px green"),
                                tbody(
                                        each(productOrderProposal.getRemarks(), remark ->
                                                tr(
                                                        td(remark.getTittle()),
                                                        td(remark.getContent())
                                                ).withStyle("text-align: center")
                                        )
                                ).withStyle("text-align: center; border: solid 1px green")
                        ).withStyle("width: 100%; background-color: white; margin-left:auto;margin-right:auto; border-collapse: collapse;")

                )
        ));
    }

    static String notifyCustomerAboutAddedNotice(NoticeDto savedNotice, ProductOrderProposalDto productOrderProposal) {

        return document(html(
                body(
                        h1("Hello, " + savedNotice.getMeetingDto().getCustomerDto().getUsername()).withStyle("text-align: center"),
                        h2("A notice has been added to meeting on day: " + savedNotice.getMeetingDto().getMeetingDate()).withStyle("text-align: center"),
                        h3("Tittle " + savedNotice.getTittle()).withStyle("text-align: center"),
                        p(savedNotice.getContent()).withStyle("text-align: center"),

                        h2("Product order details").withStyle("text-align: center"),
                        table(
                                thead(
                                        th("Product"),
                                        th("Quantity"),
                                        th("Shop"),
                                        th("Discount"),
                                        th("Delivery address")
                                ).withStyle("text-align: center; border: solid 1px green"),
                                tbody(
                                        tr(
                                                td(productOrderProposal.getProductInfo().getName() + ":" + productOrderProposal.getProductInfo().getProducerName()),
                                                td(productOrderProposal.getQuantity().toString()),
                                                td(productOrderProposal.getShopName()),
                                                td(productOrderProposal.getDiscount().toString()),
                                                td(productOrderProposal.getAddress())
                                        )
                                ).withStyle("text-align: center; border: solid 1px green")
                        ).withStyle("width: 100%; background-color: white; margin-left:auto;margin-right:auto; border-collapse: collapse;"),
                        h2("Remarks").withStyle("text-align: center; margin-top: 5vmin; margin-bottom: 5vmin"),
                        table(thead(
                                th("Tittle"),
                                th("Content")
                                ).withStyle("text-align: center; border: solid 1px green"),
                                tbody(
                                        each(productOrderProposal.getRemarks(), remark ->
                                                tr(
                                                        td(remark.getTittle()),
                                                        td(remark.getContent())
                                                ).withStyle("text-align: center")
                                        )
                                ).withStyle("text-align: center; border: solid 1px green")
                        ).withStyle("width: 100%; background-color: white; margin-left:auto;margin-right:auto; border-collapse: collapse;")

                )
                )
        );
    }

    static String generateHtmlInfoAboutManagerAccountActivation(ManagerDto activatedManager) {

        return document(html(
                body(
                        h1("Hello, " + activatedManager.getUsername()).withStyle("text-align: center; color: black"),
                        h3("Your account has been activated by admin_manager").withStyle("text-align:center; color: red")

                )
        ));
    }

    static String generateHtmlInfoAboutCustomerAccountActivation(CustomerDto activatedCustomer) {
        return document(html(
                body(
                        div(
                                h1("Hello, " + activatedCustomer.getUsername() + ". Your account has been successfully activated").withStyle("text-align: center; background-color: yellow;")
                        ).withStyle("display: inline-block; margin-top 5vmin"),
                        h5("Don't respond to this message.").withStyle("text-align: center; margin-top: 5vmin;")
                )
        ));
    }

    static String generateHtmlInfoAboutCompletionDate(ProductFailureOnGuaranteeReportDto saved) {
        return document(html(
                body(
                        h1("Hello, " + saved.getProductOrderDto().getCustomerDto().getUsername()).withStyle("text-align: center; color: black; margin-bottom: 3vmin").withStyle("background-color: yellow"),
                        div(h2("Your request has been accepted. Selected service type for product: "
                                        + saved.getProductOrderDto().getProductDto().getName() + " is: " + saved.getSelectedService()).withStyle("text-align: center; color: yellow"),
                                h2("You can pick up your product on: " + saved.getCompletionDate()).withStyle("text-align: center; color: blue")).withStyle("background-color: pink"),
                        h5("Don't respond to this message.").withStyle("text-align: center; margin-top: 5vmin; background-color: white")
                )
        ));
    }

    static String generateHtmlInfoAboutTotalPrice(String username, BigDecimal totalPrice, OrderDateBoundaryDto orderDateBoundaryDto) {

        boolean isFrom = Objects.nonNull(orderDateBoundaryDto.getFrom());
        boolean isTo = Objects.nonNull(orderDateBoundaryDto.getTo());


        var sb = new StringBuilder("Total price of bought products ");

        if (isFrom && isTo) {
            sb
                    .append("between dates: ")
                    .append(orderDateBoundaryDto.getFrom())
                    .append(",  ")
                    .append(orderDateBoundaryDto.getTo());
        } else if (isFrom) {
            sb
                    .append("from date: ")
                    .append(orderDateBoundaryDto.getFrom());
        } else if (isTo) {
            sb
                    .append("to date: ")
                    .append(orderDateBoundaryDto.getTo());
        }
        sb.append(" is: ")
                .append(totalPrice);

        return document(html(
                body(
                        h1("Hello, " + username).withStyle("text-align: center"),
                        h3(sb.toString()).withStyle("text-align: center; background-color: yellow")
                )
        ));
    }
}
