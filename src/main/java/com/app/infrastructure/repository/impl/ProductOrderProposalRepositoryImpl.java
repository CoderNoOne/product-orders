package com.app.infrastructure.repository.impl;

import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.enums.ProposalSide;
import com.app.domain.enums.ProposalStatus;
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

    @Override
    public List<ProductOrderProposal> findAllByCustomerUsernameAndStatus(String username, ProposalStatus status) {
        return jpaProductOrderProposalRepository.findAllByCustomerUsernameAndStatus(username, status);
    }

    @Override
    public List<ProductOrderProposal> findAllByCustomerUsername(String username) {
        return jpaProductOrderProposalRepository.findAllByCustomerUsername(username);
    }

    @Override
    public Optional<ProductOrderProposal> findByIdAndCustomerUsername(Long id, String username) {
        return jpaProductOrderProposalRepository.findByIdAndCustomerUsername(id, username);
    }

    @Override
    public Optional<ProductOrderProposal> findByIdAndCustomerUsernameAndSide(Long id, String username, ProposalSide side) {
        return jpaProductOrderProposalRepository.findByIdAndCustomerUsernameAndSide(id, username, side);
    }
}
