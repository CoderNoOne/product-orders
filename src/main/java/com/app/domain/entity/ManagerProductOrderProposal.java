package com.app.domain.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

//@Entity
//@SuperBuilder
//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
//@Table(name = "manager_product_order_proposals")
//public class ManagerProductOrderProposal extends ProductOrderProposal {
//
//    private BigDecimal discount;
//    private Integer daysFromOrderToPaymentDeadline;
//
//    @ManyToOne
//    @JoinColumn(name = "manager_id")
//    private Manager manager;
//
//    @ManyToOne
//    @JoinColumn(name = "customer_id")
//    private Customer customer;
//
//    public void setCustomer(Customer customer) {
//        this.customer = customer;
//    }
//
//    public void setManager(Manager manager) {
//        this.manager = manager;
//    }
//
//    public ManagerProductOrderProposal customer(Customer customer) {
//        setCustomer(customer);
//        return this;
//    }
//
//    public ManagerProductOrderProposal manager(Manager manager) {
//        setManager(manager);
//        return this;
//    }
//
//    public ManagerProductOrderProposal shop(Shop shop) {
//        super.setShop(shop);
//        return this;
//    }
//
//    public ManagerProductOrderProposal product(Product product) {
//        super.setProduct(product);
//        return this;
//    }
//}
