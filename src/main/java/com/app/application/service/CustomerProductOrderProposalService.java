package com.app.application.service;

import com.app.application.validators.impl.CreateProductOrderProposalByCustomerDtoValidator;
import com.app.domain.entity.Address;
import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.enums.ProposalSide;
import com.app.domain.enums.ProposalStatus;
import com.app.domain.repository.*;
import com.app.infrastructure.dto.CreateProductOrderProposalByCustomerDto;
import com.app.infrastructure.dto.ProductOrderProposalDto;
import com.app.infrastructure.dto.UpdateProductOrderProposalByCustomerDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.ValidationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerProductOrderProposalService {

    private final AddressRepository addressRepository;
    private final ProductOrderProposalRepository productOrderProposalRepository;
    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final CreateProductOrderProposalByCustomerDtoValidator createProductOrderProposalByCustomerDtoValidator;
    private final ObjectMapper objectMapper;

    public Long addProductOrderProposal(String username, CreateProductOrderProposalByCustomerDto createProductOrderProposalByCustomerDto) {

        var errors = createProductOrderProposalByCustomerDtoValidator.validate(createProductOrderProposalByCustomerDto);

        if (createProductOrderProposalByCustomerDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var user = customerRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No  user with username: " + username));


        var productName = createProductOrderProposalByCustomerDto.getProductInfo().getName();
        var producerName = createProductOrderProposalByCustomerDto.getProductInfo().getProducerName();

        var product = productRepository.findByNameAndProducerName(
                productName,
                producerName)
                .orElseThrow(() ->
                        new NotFoundException("No product with name: " + productName + " and producerName: " + producerName));

        var shop = shopRepository.findByName(createProductOrderProposalByCustomerDto.getShopName())
                .orElseThrow(() -> new NotFoundException("No shop with name" + createProductOrderProposalByCustomerDto.getShopName()));

        var productOrderProposal = createProductOrderProposalByCustomerDto
                .toEntity()
                .customer(user)
                .address(addressRepository.findByAddress(createProductOrderProposalByCustomerDto.getAddress())
                        .orElseGet(() -> addressRepository.save(Address.builder().address(createProductOrderProposalByCustomerDto.getAddress()).build())))
                .shop(shop)
                .product(product);


        return productOrderProposalRepository
                .save(productOrderProposal)
                .getId();
    }


    public List<ProductOrderProposalDto> getProposalsByStatus(String username, String status) {

        if (Arrays.stream(ProposalStatus.values()).noneMatch(enumStatus -> Objects.equals(enumStatus.name(), status.toUpperCase()))) {
            throw new ValidationException("Status " + status + " is not allowed");
        }

        return productOrderProposalRepository.findAllByCustomerUsernameAndStatus(username, ProposalStatus.valueOf(status.toUpperCase()))
                .stream()
                .map(ProductOrderProposal::toDto)
                .collect(Collectors.toList());

    }

    public List<ProductOrderProposalDto> getAllProposals(String username) {
        return productOrderProposalRepository.findAllByCustomerUsername(username)
                .stream()
                .map(ProductOrderProposal::toDto)
                .collect(Collectors.toList());
    }

    public Long denyProductOrderProposal(Long id, String username) {

        productOrderProposalRepository.findByIdAndCustomerUsernameAndSide(id, username, ProposalSide.MANAGER)
                .ifPresentOrElse(
                        productOrderProposal -> {
                            if (!productOrderProposal.getStatus().equals(ProposalStatus.PROPOSED)) {
                                throw new ValidationException("Product proposal should have status: proposed");
                            }
                            productOrderProposal
                                    .status(ProposalStatus.DENIED)
                                    .side(ProposalSide.CUSTOMER);
                        },
                        () -> {
                            throw new NotFoundException("No productOrder proposal with id: " + id + " addressed to customer: " + username);
                        }
                );

        return id;
    }

    public Long replyToProductOrderProposal(Long id, String username, UpdateProductOrderProposalByCustomerDto updateProductOrderProposalByCustomerDto) {

        //validation

        productOrderProposalRepository.findByIdAndCustomerUsernameAndSide(id, username, ProposalSide.MANAGER)
                .ifPresentOrElse(
                        productOrderProposal -> {
                            if (!productOrderProposal.getStatus().equals(ProposalStatus.PROPOSED)) {
                                throw new ValidationException("Product proposal should have status: proposed");
                            }
                            productOrderProposal
                                    .discount(Objects.nonNull(updateProductOrderProposalByCustomerDto.getDiscount()) ?
                                            updateProductOrderProposalByCustomerDto.getDiscount() :
                                            productOrderProposal.getDiscount())
                                    .address(addressRepository.findByAddress(updateProductOrderProposalByCustomerDto.getAddress())
                                            .orElseGet(() -> addressRepository.save(Address.builder()
                                                    .address(updateProductOrderProposalByCustomerDto.getAddress())
                                                    .build())))
                                    .side(ProposalSide.CUSTOMER);
                        },
                        () -> {
                            throw new NotFoundException("No productOrder proposal with id: " + id + " addressed to customer: " + username);
                        }
                );

        return id;
    }

    public List<ProductOrderProposalDto> getById(Long id, String username) {

        return productOrderProposalRepository.findAllRevisionsById(id)
                .stream()
                .filter(productOrderProposal -> {
                    if (!Objects.equals(productOrderProposal.getCustomer().getUsername(), username)) {
                        throw new ValidationException("You are not part of this conversation");
                    }
                    return true;
                })
                .map(ProductOrderProposal::toDto)
                .collect(Collectors.toList());
    }

    public Long acceptProductOrderProposal(Long id, String username) {

        productOrderProposalRepository.findByIdAndCustomerUsername(id, username)
                .ifPresentOrElse(
                        productOrderProposal -> {
                            if (!isReadyToAccept(productOrderProposal)) {
                                throw new ValidationException("Cannot accept. Some product order details were not negotiated with the customer");
                            }
                            if (!Objects.equals(productOrderProposal.getSide(), ProposalSide.MANAGER)) {
                                throw new ValidationException("You cannot accept your own proposal");
                            }
                            if (!Objects.equals(productOrderProposal.getStatus(), ProposalStatus.PROPOSED)) {
                                throw new ValidationException("Product proposal should have status of: PROPOSED");
                            }

                            productOrderProposal
                                    .side(ProposalSide.CUSTOMER)
                                    .status(ProposalStatus.ACCEPTED);
                        },
                        () -> {
                            throw new NotFoundException("No productOrderProposal with id: " + id + " and for customer username: " + username);
                        }
                );

        return id;
    }

    private boolean isReadyToAccept(ProductOrderProposal productOrderProposal) {
        ProductOrderProposalDto productOrderProposalDto = productOrderProposal.toDto();
        Map<String, Object> objectFieldsValues = objectMapper.convertValue(productOrderProposalDto, new TypeReference<>() {
        });

        return objectFieldsValues.entrySet()
                .stream()
                .filter(e -> !e.getKey().equals("remarks"))
                .allMatch(e -> Objects.nonNull(e.getValue()));

    }
}
