package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "addresses")
public class Address extends BaseEntity {

    @Column(unique = true)
    private String address;

    public String getAddress() {
        return address;
    }
}
