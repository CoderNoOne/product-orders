package com.app.domain.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "customers")
public class Customer extends User {

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    private Integer age;

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Integer getAge() {
        return age;
    }

}
