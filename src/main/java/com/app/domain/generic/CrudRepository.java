package com.app.domain.generic;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    List<T> findAll();
    Optional<T> findOne(ID id);
    /*Optional<T>*/ T save(T t);
}
