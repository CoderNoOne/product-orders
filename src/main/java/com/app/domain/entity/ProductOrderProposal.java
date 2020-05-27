package com.app.domain.entity;

import com.app.domain.embbedable.ProposalRemark;
import com.app.domain.enums.ProposalSide;
import com.app.domain.enums.ProposalStatus;
import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.ProductOrderProposalDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.net.Proxy;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "product_orders_proposal")

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProductOrderProposal extends BaseEntity {

    private BigDecimal discount;
    private Integer daysFromOrderToPaymentDeadline;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Enumerated(EnumType.STRING)
    private ProposalSide side;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ElementCollection
    @CollectionTable(name = "proposal_remarks")
    private List<ProposalRemark> remarks;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

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

    public Manager getManager() {
        return customer.getManager();
    }

    public ProductOrderProposal customer(Customer customer) {
        setCustomer(customer);
        return this;
    }


    public ProductOrderProposal shop(Shop shop) {
        setShop(shop);
        return this;
    }

    public ProductOrderProposal product(Product product) {
        setProduct(product);
        return this;
    }

    public ProductOrderProposal discount(BigDecimal discount) {
        this.discount = discount;
        return this;
    }

    public ProductOrderProposal address(Address address) {
        this.address = address;
        return this;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public ProductOrderProposal status(ProposalStatus status) {
        this.status = status;
        return this;
    }


    public ProductOrderProposal side(ProposalSide side) {
        this.side = side;
        return this;
    }
}
