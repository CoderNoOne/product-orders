package com.app.domain.embbedable;

import com.app.domain.enums.ProposalSide;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProposalRemark {

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private String tittle;

    @Enumerated(EnumType.STRING)
    private ProposalSide side;

    @Column(length = 2000, nullable = false)
    private String content;

}
