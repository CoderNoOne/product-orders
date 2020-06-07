package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Complaint;
import com.app.domain.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaComplaintRepository extends JpaRepository<Complaint, Long> {

    @Query("select c from Complaint c where c.productOrder.customer.manager.username = :username and c.id = :id")
    Optional<Complaint> findByIdAndMangerUsername(Long id, String username);

    @Query("select c from Complaint c where c.productOrder.customer.manager.username = :username")
    List<Complaint> findAllByManagerUsername(String username);

    @Query("select c from Complaint c where c.productOrder.customer.manager.username = :managerUsername and c.status = :status and c.id = :complaintId")
    Optional<Complaint> findByIdAndMangerUsernameAndStatus(Long complaintId, String managerUsername, ComplaintStatus status);


    Optional<Complaint> findByProductOrderId(Long productOrderId);

}
