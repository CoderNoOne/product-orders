package com.app.domain.repository;

import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.generic.CrudRepository;

import java.util.List;

public interface ProductOrderProposalRepository extends CrudRepository<ProductOrderProposal, Long> {

    List<ProductOrderProposal> getAllByManagerUsername(String username);
}
