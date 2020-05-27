package com.app.domain.repository;

import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.enums.ProposalSide;
import com.app.domain.enums.ProposalStatus;
import com.app.domain.generic.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductOrderProposalRepository extends CrudRepository<ProductOrderProposal, Long> {

    List<ProductOrderProposal> getAllByManagerUsername(String username);

    List<ProductOrderProposal> findAllByCustomerUsernameAndStatus(String username, ProposalStatus status);

    List<ProductOrderProposal> findAllByCustomerUsername(String username);

    Optional<ProductOrderProposal> findByIdAndCustomerUsername(Long id, String username);

    Optional<ProductOrderProposal> findByIdAndCustomerUsernameAndSide(Long id, String username, ProposalSide side);
}
