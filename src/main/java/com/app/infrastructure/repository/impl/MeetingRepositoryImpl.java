package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Meeting;
import com.app.domain.repository.MeetingRepository;
import com.app.infrastructure.repository.jpa.JpaMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MeetingRepositoryImpl implements MeetingRepository {

    private final JpaMeetingRepository jpaMeetingRepository;

    @Override
    public List<Meeting> findAll() {
        return jpaMeetingRepository.findAll();
    }

    @Override
    public Optional<Meeting> findOne(Long id) {
        return jpaMeetingRepository.findById(id);
    }

    @Override
    public Meeting save(Meeting meeting) {
        return jpaMeetingRepository.save(meeting);
    }

    @Override
    public void delete(Meeting meeting) {
        jpaMeetingRepository.delete(meeting);
    }
}
