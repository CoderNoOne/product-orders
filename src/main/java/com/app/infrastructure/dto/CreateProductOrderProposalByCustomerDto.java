package com.app.infrastructure.dto;

import com.app.domain.entity.*;
import com.app.domain.enums.ProposalSide;
import com.app.domain.enums.ProposalStatus;
import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductOrderProposalByCustomerDto {

    private ProductInfo productInfo;
    private Integer quantity;
    private CreateProposalRemarkDto remark;
    private String shopName;
    private String address;

    public ProductOrderProposal toEntity() {
        return
                ProductOrderProposal.builder()
                        .product(Objects.nonNull(productInfo) ? Product.builder()
                                .name(Objects.nonNull(productInfo.getName()) ? productInfo.getName() : null)
                                .producer(Objects.nonNull(productInfo.getProducerName()) ? Producer.builder()
                                        .name(productInfo.getProducerName())
                                        .build() : null)
                                .build() : null)
                        .quantity(quantity)
                        .remarks(Objects.nonNull(remark) ? new ArrayList<>(List.of(remark.toEntity())) : new ArrayList<>())
                        .shop(Objects.nonNull(shopName) ? Shop.builder()
                                .name(shopName)
                                .build() : null)
                        .status(ProposalStatus.PROPOSED)
                        .deliveryAddress(Objects.nonNull(address) ? Address.builder().build() : null)
                        .side(ProposalSide.CUSTOMER)
                        .build();
    }
}
