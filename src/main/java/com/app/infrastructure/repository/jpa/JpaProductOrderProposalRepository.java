package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.ProductOrderProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaProductOrderProposalRepository extends JpaRepository <ProductOrderProposal, Long> {

    @Query("select p from ProductOrderProposal p where p..manager.username = :username")
    List<ProductOrderProposal> findAllByManagerUsername(String username);
}
