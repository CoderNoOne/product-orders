package com.app.application.service;

import com.app.application.validators.impl.CreateManagerProductOrderProposalDtoValidator;
import com.app.application.validators.impl.ManagerUpdateProductProposalDtoValidator;
import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.repository.*;
import com.app.infrastructure.dto.CreateManagerProductOrderProposalDto;
import com.app.infrastructure.dto.ManagerUpdateProductOrderProposalDto;
import com.app.infrastructure.dto.ProductOrderProposalDto;
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
    private final CreateManagerProductOrderProposalDtoValidator createManagerProductOrderProposalDtoValidator;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final ManagerRepository managerRepository;
    private final CustomerRepository customerRepository;

    public List<ProductOrderProposalDto> getAllProposals(String managerUsername) {

        if (Objects.isNull(managerUsername)) {
            throw new NullReferenceException("Manager username is null");
        }

        return productOrderProposalRepository.getAllByManagerUsername(managerUsername)
                .stream()
                .map(ProductOrderProposal::toDto)
                .collect(Collectors.toList());
    }

    // TODO: 19.05.2020 change
    public Long updateProductProposal(Long id, ManagerUpdateProductOrderProposalDto managerUpdateProductOrderProposalDto) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Id is null");
        }

        var errors = managerUpdateProductProposalDtoValidator.validate(managerUpdateProductOrderProposalDto);

        if (managerUpdateProductProposalDtoValidator.hasErrors()) {
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

    public Long addManagerProductOrderProposal(String managerUsername, CreateManagerProductOrderProposalDto createManagerProductOrderProposalDto) {

        var errors = createManagerProductOrderProposalDtoValidator.validate(createManagerProductOrderProposalDto);

        if (createManagerProductOrderProposalDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var manager = managerRepository.findByUsername(managerUsername)
                .orElseThrow(() -> new NotFoundException("No  manager with username: " + managerUsername));

        var customer = customerRepository.findByUsername(createManagerProductOrderProposalDto.getCustomerUsername())
                .orElseThrow(() -> new NotFoundException("No  customer with username: " + createManagerProductOrderProposalDto.getCustomerUsername()));

        if (!manager.getCustomers().contains(customer)) {

            throw new ValidationException("You cannot add proposal to customer: " + createManagerProductOrderProposalDto.getCustomerUsername());

        }

        var managerProductOrderProposal = createManagerProductOrderProposalDto.toEntity();

        var productName = createManagerProductOrderProposalDto.getProductInfo().getName();

        var producerName = createManagerProductOrderProposalDto.getProductInfo().getProducerName();

        var product = productRepository.findByNameAndProducerName(
                productName,
                producerName)
                .orElseThrow(() ->
                        new NotFoundException("No product with name: " + productName + " and producerName: " + producerName));


        var shop = shopRepository.findByName(createManagerProductOrderProposalDto.getShopName())
                .orElseThrow(() -> new NotFoundException("No shop with name" + createManagerProductOrderProposalDto.getShopName()));

        var productCountInAllStocks = shopRepository.findProductCountInAllStocks(shop.getId(), product.getId());

        if (productCountInAllStocks < createManagerProductOrderProposalDto.getQuantity()) {
            throw new ValidationException("Not enough product quantity in shop stores");
        }

        managerProductOrderProposal.setShop(shop);
        managerProductOrderProposal.setManager(manager);
        managerProductOrderProposal.setCustomer(customer);
        managerProductOrderProposal.setProduct(product);

        return productOrderProposalRepository
                .save(managerProductOrderProposal)
                .getId();

    }
}
