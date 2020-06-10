package com.app.application.service;

import com.app.domain.entity.Manager;
import com.app.domain.repository.ManagerRepository;
import com.app.infrastructure.dto.ManagerDto;
import com.app.infrastructure.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;

    public ManagerDto activate(Long id) {

        var managerWrapper = new AtomicReference<ManagerDto>();

        managerRepository.findOne(id)
                .ifPresentOrElse(
                        manager -> {
                            manager.setEnabled(true);
                            managerWrapper.set(manager.toDto());
                        },
                        () -> {
                            throw new NotFoundException("No manager with id: " + id);
                        }
                );

        return managerWrapper.get();
    }

    public List<ManagerDto> getAll(Boolean enabled) {
        return managerRepository.findAll()
                .stream()
                .filter(manager -> !Objects.nonNull(enabled) || Objects.equals(manager.getEnabled(), enabled))
                .map(Manager::toDto)
                .collect(Collectors.toList());
    }
}
