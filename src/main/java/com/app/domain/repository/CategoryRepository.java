package com.app.domain.repository;

import com.app.domain.entity.Category;
import com.app.domain.generic.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    Optional<Category> findByName(String name);
}
