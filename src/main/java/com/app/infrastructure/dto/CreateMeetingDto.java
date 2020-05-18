package com.app.infrastructure.dto;

import com.app.domain.entity.Meeting;
import com.app.domain.entity.ProductOrder;
import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.enums.MeetingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateMeetingDto {

    private LocalDate meetingDate;
    private Long productOrderProposalId;

    public Meeting toEntity() {
        return Meeting.builder()
                .meetingDate(meetingDate)
                .orderProposal(ProductOrderProposal.builder().id(productOrderProposalId).build())
                .status(MeetingStatus.PROPOSED)
                .notices(new ArrayList<>())
                .build();
    }
}
