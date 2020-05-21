package com.app.domain.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "customer_product_order_proposals")
public class CustomerProductOrderProposal extends ProductOrderProposal {

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
