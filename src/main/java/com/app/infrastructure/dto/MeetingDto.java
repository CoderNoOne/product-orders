package com.app.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDto {

    private Long id;
    private LocalDate meetingDate;
    private String status;
    private Long orderProposalId;
    private CustomerDto customerDto;
}
