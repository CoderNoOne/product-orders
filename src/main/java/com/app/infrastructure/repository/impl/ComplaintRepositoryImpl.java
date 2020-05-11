package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Complaint;
import com.app.domain.repository.ComplaintRepository;
import com.app.infrastructure.repository.jpa.JpaComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ComplaintRepositoryImpl implements ComplaintRepository {

    private final JpaComplaintRepository jpaComplaintRepository;

    @Override
    public List<Complaint> findAll() {
        return jpaComplaintRepository.findAll();
    }

    @Override
    public Optional<Complaint> findOne(Long id) {
        return jpaComplaintRepository.findById(id);
    }

    @Override
    public Complaint save(Complaint complaint) {
        return jpaComplaintRepository.save(complaint);
    }

    @Override
    public Optional<Complaint> findByIdAndManagerUsername(Long id, String username) {
        return jpaComplaintRepository.findByIdAndMangerUsername(id, username);
    }

    @Override
    public List<Complaint> findAllByManagerUsername(String username) {
        return jpaComplaintRepository.findAllByManagerUsername(username);
    }
}
