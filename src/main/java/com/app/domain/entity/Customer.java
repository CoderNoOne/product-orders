package com.app.domain.entity;

import com.app.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;


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

    @Enumerated(EnumType.STRING)
    private Gender gender;

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
