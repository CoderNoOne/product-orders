package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Notice;
import com.app.domain.repository.NoticeRepository;
import com.app.infrastructure.repository.jpa.JpaNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final JpaNoticeRepository jpaNoticeRepository;

    @Override
    public List<Notice> findAll() {
        return jpaNoticeRepository.findAll();
    }

    @Override
    public Optional<Notice> findOne(Long id) {
        return jpaNoticeRepository.findById(id);
    }

    @Override
    public Notice save(Notice notice) {
        return jpaNoticeRepository.save(notice);
    }
}
