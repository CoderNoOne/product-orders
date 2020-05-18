package com.app.domain.entity;

import com.app.domain.embbedable.ProposalRemark;
import com.app.domain.enums.ProposalStatus;
import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.ProductOrderProposalDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "product_orders_proposal")
@SuperBuilder
@NoArgsConstructor
public class ProductOrderProposal extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private BigDecimal discount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ElementCollection
    @CollectionTable(name = "proposal_remarks")
    private List<ProposalRemark> remarks;


    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public ProductOrderProposalDto toDto() {
        return ProductOrderProposalDto.builder()
                .customerUsername(Objects.nonNull(customer) ? customer.getUsername() : null)
                .productInfo(Objects.nonNull(product) ? ProductInfo.builder()
                        .name(product.getName())
                        .producerName(Objects.nonNull(product.getProducer()) ? product.getProducer().getName() : null)
                        .build() : null)
                .quantity(quantity)
                .shopName(Objects.nonNull(shop) ? shop.getName() : null)
                .proposalStatus(status.toString())
                .remarks(remarks)
                .build();
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<ProposalRemark> getRemarks() {
        return remarks;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public ProposalStatus getStatus() {
        return status;
    }

    public void setStatus(ProposalStatus status) {
        this.status = status;
    }
}
