package com.app.application.service;

import com.app.application.validators.impl.CreateProductOrderProposalByCustomerDtoValidator;
import com.app.domain.entity.Product;
import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.entity.Shop;
import com.app.domain.entity.User;
import com.app.domain.repository.*;
import com.app.infrastructure.dto.CreateProductOrderProposalByCustomerDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerProductOrderProposalService {

    private final ProductOrderProposalRepository productOrderProposalRepository;
    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final CreateProductOrderProposalByCustomerDtoValidator createProductOrderProposalByCustomerDtoValidator;

    public Long addProductOrderProposal(String username, CreateProductOrderProposalByCustomerDto createProductOrderProposalByCustomerDto) {

        var errors = createProductOrderProposalByCustomerDtoValidator.validate(createProductOrderProposalByCustomerDto);

        if (createProductOrderProposalByCustomerDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var user = customerRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No  user with username: " + username));

        var productOrderProposal = createProductOrderProposalByCustomerDto.toEntity();
        productOrderProposal.setCustomer(user);

        var productName = createProductOrderProposalByCustomerDto.getProductInfo().getName();
        var producerName = createProductOrderProposalByCustomerDto.getProductInfo().getProducerName();

        var product = productRepository.findByNameAndProducerName(
                productName,
                producerName)
                .orElseThrow(() ->
                        new NotFoundException("No product with name: " + productName + " and producerName: " + producerName));

        productOrderProposal.setProduct(product);

        var shop = shopRepository.findByName(createProductOrderProposalByCustomerDto.getShopName())
                .orElseThrow(() -> new NotFoundException("No shop with name" + createProductOrderProposalByCustomerDto.getShopName()));

        productOrderProposal.setShop(shop);

        return productOrderProposalRepository
                .save(productOrderProposal)
                .getId();
    }


}
