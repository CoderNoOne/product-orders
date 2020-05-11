package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaCategoryRepository extends JpaRepository<Category,Long> {
    Optional<Category> findByName(String name);
}
