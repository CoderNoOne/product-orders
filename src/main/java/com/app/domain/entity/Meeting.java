package com.app.domain.entity;

import com.app.domain.enums.MeetingStatus;
import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.MeetingDto;
import com.app.infrastructure.dto.NoticeDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "meetings")
public class Meeting extends BaseEntity {

    @OneToMany(mappedBy = "meeting")
    private List<Notice> notices;

//    @ManyToOne
//    @JoinColumn(name = "customer_id")
//    private Customer customer;
//
//    @ManyToOne
//    @JoinColumn(name = "manager_id")
//    private Manager manager;

    @OneToOne
    @JoinColumn(name = "proposal_id")
    private ProductOrderProposal orderProposal;

    @Enumerated(EnumType.STRING)
    private MeetingStatus status;

    private LocalDate meetingDate;

    public MeetingDto toDto() {

        return MeetingDto.builder()
                .id(getId())
                .meetingDate(meetingDate)
                .status(Objects.nonNull(status) ? status.name() : null)
                .orderProposalId(Objects.nonNull(orderProposal) ? orderProposal.getId() : null)
                .build();
    }

    public void setProductOrderProposal(ProductOrderProposal orderProposal) {
        this.orderProposal = orderProposal;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public ProductOrderProposal getProposalProductOrder() {
        return orderProposal;
    }

    public List<Notice> getNotices() {
        return notices;
    }
}
