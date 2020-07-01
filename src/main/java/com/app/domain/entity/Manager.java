package com.app.domain.entity;

import com.app.infrastructure.dto.ManagerDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "managers")

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Manager extends User {

    @OneToMany
    @JoinTable(name = "managers_customers",
            joinColumns = @JoinColumn(name = "manager_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"))
    private List<Customer> customers;

    public List<Customer> getCustomers() {
        return customers;
    }

    public ManagerDto toDto() {

        return ManagerDto.builder()
                .id(getId())
                .email(getEmail())
                .username(getUsername())
                .customers(Objects.nonNull(customers) ? customers.stream().map(Customer::toDto).collect(Collectors.toList()) : new ArrayList<>())
                .enabled(super.getEnabled())
                .build();
    }
}


