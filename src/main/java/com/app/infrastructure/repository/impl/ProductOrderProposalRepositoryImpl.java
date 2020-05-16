package com.app.infrastructure.repository.impl;

import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.repository.ProductOrderProposalRepository;
import com.app.infrastructure.repository.jpa.JpaProductOrderProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductOrderProposalRepositoryImpl implements ProductOrderProposalRepository {

    private final JpaProductOrderProposalRepository jpaProductOrderProposalRepository;

    @Override
    public List<ProductOrderProposal> findAll() {
        return jpaProductOrderProposalRepository.findAll();
    }

    @Override
    public Optional<ProductOrderProposal> findOne(Long id) {
        return jpaProductOrderProposalRepository.findById(id);
    }

    @Override
    public ProductOrderProposal save(ProductOrderProposal productOrderProposal) {
        return jpaProductOrderProposalRepository.save(productOrderProposal);
    }

    @Override
    public List<ProductOrderProposal> getAllByManagerUsername(String username) {
        return jpaProductOrderProposalRepository.findAllByManagerUsername(username);
    }
}
