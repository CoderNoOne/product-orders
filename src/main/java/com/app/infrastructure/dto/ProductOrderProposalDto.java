package com.app.infrastructure.dto;

import com.app.domain.embbedable.ProposalRemark;
import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductOrderProposalDto {

    private ProductInfo productInfo;
    private Integer quantity;
    private String shopName;
    private String customerUsername;
    private List<ProposalRemark> remarks;
    private String proposalStatus;
    private String side;
    private String address;
    private BigDecimal discount;
}
