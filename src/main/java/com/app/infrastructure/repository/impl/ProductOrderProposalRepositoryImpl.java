package com.app.infrastructure.repository.impl;

import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.enums.ProposalSide;
import com.app.domain.enums.ProposalStatus;
import com.app.domain.repository.ProductOrderProposalRepository;
import com.app.infrastructure.repository.jpa.JpaProductOrderProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<ProductOrderProposal> findAllRevisionsById(Long id) {

        return jpaProductOrderProposalRepository.findRevisions(id).getContent()
                .stream()
                .sorted(Comparator.comparing(Revision::getRequiredRevisionNumber))
                .map(Revision::getEntity)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<ProductOrderProposal> findByIdAndManagerUsername(Long id, String username) {
        return jpaProductOrderProposalRepository.findByIdAndCustomerManagerUsername(id, username);
    }

    @Override
    public Optional<ProductOrderProposal> findByIdAndManagerUsernameAndStatus(Long acceptedProductOrderProposalId, String managerUsername, ProposalStatus status) {
        return jpaProductOrderProposalRepository.findByIdAndCustomerManagerUsernameAndStatus(acceptedProductOrderProposalId, managerUsername, status);
    }
}
