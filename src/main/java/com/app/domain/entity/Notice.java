package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "notices")
public class Notice extends BaseEntity {

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String tittle;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;
}
