package com.app.application.service;

import com.app.application.validators.impl.ManagerUpdateProductProposalDtoValidator;
import com.app.domain.entity.Product;
import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.entity.Shop;
import com.app.domain.repository.ProductOrderProposalRepository;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.ShopRepository;
import com.app.infrastructure.dto.ManagerUpdateProductOrderProposalDto;
import com.app.infrastructure.dto.ProductOrderProposalDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
import com.app.infrastructure.exception.NullReferenceException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagerProductOrderProposalService {

    private final ProductOrderProposalRepository productOrderProposalRepository;
    private final ManagerUpdateProductProposalDtoValidator managerUpdateProductProposalDtoValidator;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    public List<ProductOrderProposalDto> getAllProposals(String managerUsername) {

        if (Objects.isNull(managerUsername)) {
            throw new NullReferenceException("Manager username is null");
        }

        return productOrderProposalRepository.getAllByManagerUsername(managerUsername)
                .stream()
                .map(ProductOrderProposal::toDto)
                .collect(Collectors.toList());
    }

    public Long updateProductProposal(Long id, ManagerUpdateProductOrderProposalDto managerUpdateProductOrderProposalDto) {

        if(Objects.isNull(id)){
            throw new NullIdValueException("Id is null");
        }

        var errors = managerUpdateProductProposalDtoValidator.validate(managerUpdateProductOrderProposalDto);

        if(managerUpdateProductProposalDtoValidator.hasErrors()){
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var productOrderProposal = managerUpdateProductOrderProposalDto.toEntity();

        var productOrderProposalFromDb = productOrderProposalRepository.findOne(id).orElseThrow(() -> new NotFoundException("No proposal with id: " + id));

        productOrderProposalFromDb.getRemarks().addAll(productOrderProposal.getRemarks());

        var productInfo = managerUpdateProductOrderProposalDto.getProductInfo();
        var productName = productInfo.getName();
        var producerName = productInfo.getProducerName();

        var product = productRepository.findByNameAndProducerName(productName, producerName)
                .orElseThrow(() -> new NotFoundException(""));

        productOrderProposalFromDb.setProduct(product);
        productOrderProposalFromDb.setQuantity(productOrderProposal.getQuantity());

        var shop = shopRepository.findByName(managerUpdateProductOrderProposalDto.getShopName())
                .orElseThrow(() -> new NotFoundException("No shop with name: " + managerUpdateProductOrderProposalDto.getShopName()));

        productOrderProposalFromDb.setShop(shop);
        productOrderProposalFromDb.setStatus(productOrderProposal.getStatus());

        return productOrderProposalFromDb.getId();
    }
}
