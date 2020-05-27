package com.app.infrastructure.dto;

import com.app.domain.embbedable.ProposalRemark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProposalRemarkDto {

    private String tittle;
    private String content;

    public ProposalRemark toEntity(){
        return ProposalRemark.builder()
                .issueDate(LocalDate.now())
                .content(content)
                .tittle(tittle)
                .build();
    }

}
