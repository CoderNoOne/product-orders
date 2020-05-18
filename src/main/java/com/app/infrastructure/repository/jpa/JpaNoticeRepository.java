package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaNoticeRepository extends JpaRepository<Notice, Long> {
}
