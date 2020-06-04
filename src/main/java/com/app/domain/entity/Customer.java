package com.app.domain.entity;

import com.app.domain.enums.Gender;
import com.app.infrastructure.dto.CustomerDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Objects;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Table(name = "customers")
public class Customer extends User {

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private boolean enabled;

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Integer getAge() {
        return age;
    }

    public CustomerDto toDto() {

        return CustomerDto.builder()
                .id(getId())
                .username(getUsername())
                .email(getEmail())
                .age(age)
                .gender(Objects.nonNull(gender) ? gender.name() : null)
                .build();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
