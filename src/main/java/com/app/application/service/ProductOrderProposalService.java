package com.app.application.service;

import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.repository.ProductOrderProposalRepository;
import com.app.infrastructure.dto.ProductOrderProposalDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductOrderProposalService {

    private final ProductOrderProposalRepository productOrderProposalRepository;

    public ProductOrderProposalDto getById(Long id) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Id is null");
        }

        return productOrderProposalRepository.findOne(id)
                .map(ProductOrderProposal::toDto)
                .orElseThrow(() -> new NotFoundException("No productOrderProposal with id: " + id));
    }
}

