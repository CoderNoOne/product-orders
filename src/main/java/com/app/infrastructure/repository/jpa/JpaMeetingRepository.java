package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMeetingRepository extends JpaRepository <Meeting, Long> {
}
