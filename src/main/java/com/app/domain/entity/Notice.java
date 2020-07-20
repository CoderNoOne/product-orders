package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.NoticeDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "notices")
public class Notice extends BaseEntity {

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false, length = 50)
    private String tittle;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    public NoticeDto toDto() {

        return NoticeDto.builder()
                .id(getId())
                .content(content)
                .tittle(tittle)
                .meetingDto(Objects.nonNull(meeting) ? meeting.toDto() : null)
                .build();
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public Meeting getMeeting() {
        return meeting;
    }
}
