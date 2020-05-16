package com.app.infrastructure.dto;

import com.app.domain.embbedable.ProposalRemark;
import com.app.domain.entity.Producer;
import com.app.domain.entity.Product;
import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.entity.Shop;
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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ManagerUpdateProductOrderProposalDto {

    private BigDecimal discount;
    private ProductInfo productInfo;
    private Integer quantity;
    private String shopName;
    private String proposalStatus;
    private List<ProposalRemark> remarks;

    public ProductOrderProposal toEntity() {

        return ProductOrderProposal.builder()
                .status(ProposalStatus.PROPOSED)
                .shop(Objects.nonNull(shopName) ?
                        Shop.builder()
                                .name(shopName)
                                .build() : null)
                .quantity(quantity)
                .product(Objects.nonNull(productInfo) ?
                        Product.builder()
                                .name(productInfo.getName())
                                .producer(Objects.nonNull(productInfo.getProducerName()) ?
                                        Producer.builder().name(productInfo.getProducerName()).build()
                                        : null
                                )
                                .build() : null)
                .discount(discount)
                .remarks(Objects.nonNull(remarks) ? remarks : new ArrayList<>())
                .build();
    }
}
