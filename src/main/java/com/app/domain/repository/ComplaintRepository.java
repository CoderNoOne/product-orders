package com.app.domain.repository;

import com.app.domain.entity.Complaint;
import com.app.domain.enums.ComplaintStatus;
import com.app.domain.generic.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends CrudRepository<Complaint, Long> {

    Optional<Complaint> findByIdAndManagerUsername(Long id, String username);

    List<Complaint> findAllByManagerUsername(String username);

    Optional<Complaint> findByIdAndManagerUsernameAndStatus(Long complaintId, String managerUsername, ComplaintStatus confirmed);
}
