package com.app.domain.repository;

import com.app.domain.entity.Meeting;
import com.app.domain.generic.CrudRepository;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {
    void delete(Meeting meeting);
}
