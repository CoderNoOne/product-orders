package com.app.domain.generic;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@MappedSuperclass

@EqualsAndHashCode
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private LocalDateTime lastUpdatedTime;

    public void setId(Long id) {
        this.id = id;
    }
}
