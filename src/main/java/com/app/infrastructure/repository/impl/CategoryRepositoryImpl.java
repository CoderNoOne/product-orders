package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Category;
import com.app.domain.repository.CategoryRepository;
import com.app.infrastructure.repository.jpa.JpaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JpaCategoryRepository jpaCategoryRepository;

    @Override
    public Optional<Category> findByName(String name) {
        return jpaCategoryRepository.findByName(name);
    }

    @Override
    public List<Category> findAll() {
        return jpaCategoryRepository.findAll();
    }

    @Override
    public Optional<Category> findOne(Long id) {
        return jpaCategoryRepository.findById(id);
    }

    @Override
    public Category save(Category category) {
        return jpaCategoryRepository.save(category);
    }
}
