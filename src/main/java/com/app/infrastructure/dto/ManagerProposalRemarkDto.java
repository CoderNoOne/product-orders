//package com.app.infrastructure.dto;
//
//import com.app.domain.embbedable.ProposalRemark;
//import com.app.domain.enums.ProposalSide;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class ManagerProposalRemarkDto {
//
//    private String tittle;
//    private String content;
//
//    public ProposalRemark toEntity(){
//        return ProposalRemark.builder()
//                .side(ProposalSide.MANAGER)
//                .tittle(tittle)
//                .content(content)
//                .build();
//    }
//
//    public ProposalRemark toProposalRemark(){
//        return ProposalRemark.builder()
//                .content(content)
//                .tittle(tittle)
//                .issueDate(LocalDate.now())
//                .side(ProposalSide.MANAGER)
//                .build();
//    }
//}
