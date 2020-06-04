package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "register_verification_tokens")

@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RegisterVerificationToken extends BaseEntity {

    private String token;
    private LocalDateTime expirationTime;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RegisterVerificationToken token(String token){
        this.token = token;
        return this;
    }

    public RegisterVerificationToken expirationTime(LocalDateTime expirationTime){
        this.expirationTime = expirationTime;
        return this;
    }

    public Customer getCustomer() {
        return customer;
    }
}
