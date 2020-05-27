package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.enums.ProposalSide;
import com.app.domain.enums.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

public interface JpaProductOrderProposalRepository extends JpaRepository <ProductOrderProposal, Long>, RevisionRepository <ProductOrderProposal, Long, Long> {

    @Query("select p from ProductOrderProposal p where p.customer.manager.username = :username")
    List<ProductOrderProposal> findAllByManagerUsername(String username);

    List<ProductOrderProposal> findAllByCustomerUsernameAndStatus(String username, ProposalStatus status);

    List<ProductOrderProposal> findAllByCustomerUsername(String username);

    Optional<ProductOrderProposal> findByIdAndCustomerUsername(Long id, String username);

    Optional<ProductOrderProposal> findByIdAndCustomerUsernameAndSide(Long id, String username, ProposalSide side);


}
