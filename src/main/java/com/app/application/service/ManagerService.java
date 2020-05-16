package com.app.application.service;

import com.app.domain.repository.ManagerRepository;
import com.app.infrastructure.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;

    public Long activate(Long id) {

        managerRepository.findOne(id)
                .ifPresentOrElse(
                        manager -> manager.setEnabled(true),
                        () -> {
                            throw new NotFoundException("No manager witt id: " + id);
                        }
                );

        return id;
    }
}
