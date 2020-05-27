package com.app.domain.embbedable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProposalRemark {

    private LocalDate issueDate;
    private String tittle;
    private String content;

    public ProposalRemark issueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
        return this;
    }
}
