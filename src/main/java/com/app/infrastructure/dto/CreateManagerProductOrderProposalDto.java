package com.app.infrastructure.dto;

import com.app.domain.entity.*;
import com.app.domain.enums.ProposalStatus;
import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateManagerProductOrderProposalDto {

    private ProductInfo productInfo;
    private Integer quantity;
    private BigDecimal discount;
    private Integer daysFromOrderToPaymentDeadline;
    private ManagerProposalRemarkDto remark;
    private String shopName;
    private String customerUsername;

    public ManagerProductOrderProposal toEntity() {


        return
                ManagerProductOrderProposal.builder()
                        .product(Objects.nonNull(productInfo) ? Product.builder()
                                .name(Objects.nonNull(productInfo.getName()) ? productInfo.getName() : null)
                                .producer(Objects.nonNull(productInfo.getProducerName()) ? Producer.builder()
                                        .name(productInfo.getProducerName())
                                        .build() : null)
                                .build() : null)
                        .quantity(quantity)
                        .remarks(Objects.nonNull(remark) ? List.of(remark.toProposalRemark()) : new ArrayList<>())
                        .shop(Objects.nonNull(shopName) ? Shop.builder()
                                .name(shopName)
                                .build() : null)
                        .status(ProposalStatus.PROPOSED)
                        .discount(discount)
                        .daysFromOrderToPaymentDeadline(daysFromOrderToPaymentDeadline)
                        .build();
    }
}
