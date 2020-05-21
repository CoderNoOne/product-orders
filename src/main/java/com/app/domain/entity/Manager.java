package com.app.domain.entity;

import com.app.infrastructure.dto.ManagerDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "managers")
public class Manager extends User {

    @OneToMany
    @JoinTable(name = "managers_customers",
            joinColumns = @JoinColumn(name = "manager_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"))
    private List<Customer> customers;

    private Boolean enabled;

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public ManagerDto toDto() {

        return ManagerDto.builder()
                .id(getId())
                .email(getEmail())
                .username(getUsername())
                .customers(Objects.nonNull(customers) ? customers.stream().map(Customer::toDto).collect(Collectors.toList()) : new ArrayList<>())
                .enabled(enabled)
                .build();
    }
}
