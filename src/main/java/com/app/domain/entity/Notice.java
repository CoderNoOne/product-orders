package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "notices")
public class Notice extends BaseEntity {

    private String content;

    @OneToOne
    @JoinColumn(name = "product_order_id", referencedColumnName = "id")
    private ProductOrder order;

    @OneToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;
}
