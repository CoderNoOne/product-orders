package com.app.infrastructure.dto;

import com.app.domain.entity.Meeting;
import com.app.domain.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoticeDto {

    private Long meetingId;
    private String tittle;
    private String content;

    public Notice toEntity() {
        return Notice.builder()
                .tittle(tittle)
                .content(content)
                .meeting(Meeting.builder()
                        .id(meetingId)
                        .build())
                .build();
    }
}
