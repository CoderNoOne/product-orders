package com.app.application.service;

import com.app.application.validators.impl.CreateManagerProductOrderProposalDtoValidator;
import com.app.application.validators.impl.ManagerUpdateProductProposalDtoValidator;
import com.app.domain.entity.ProductOrderProposal;
import com.app.domain.enums.ProposalSide;
import com.app.domain.enums.ProposalStatus;
import com.app.domain.repository.*;
import com.app.infrastructure.dto.*;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
import com.app.infrastructure.exception.NullReferenceException;
import com.app.infrastructure.exception.ValidationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.webresources.ClasspathURLStreamHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
    private final ObjectMapper objectMapper;

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

        managerProductOrderProposal
                .shop(shop)
                .customer(customer)
                .product(product);

        return productOrderProposalRepository
                .save(managerProductOrderProposal)
                .getId();

    }

    public List<ProductOrderProposalDto> getRevisionsById(Long id, String username) {

        return productOrderProposalRepository
                .findAllRevisionsById(id)
                .stream()
                .filter(productOrderProposal -> {
                    if (!Objects.equals(productOrderProposal.getManager().getUsername(), username)) {
                        throw new ValidationException("You are not part of this conversation");
                    }
                    return true;
                })
                .map(ProductOrderProposal::toDto)
                .collect(Collectors.toList());
    }

    public Long denyProductOrderProposal(Long id, String username) {

        productOrderProposalRepository.findByIdAndManagerUsername(id, username)
                .ifPresentOrElse(
                        productOrderProposal -> {
                            if (!Objects.equals(productOrderProposal.getSide(), ProposalSide.CUSTOMER)) {
                                throw new ValidationException("You cannot respond to your own proposal");
                            }
                            if (!Objects.equals(productOrderProposal.getStatus(), ProposalStatus.PROPOSED)) {
                                throw new ValidationException("This proposal is not actual");
                            }
                            productOrderProposal
                                    .status(ProposalStatus.DENIED)
                                    .side(ProposalSide.MANAGER);
                        }
                        ,
                        () -> {
                            throw new NotFoundException("");
                        }
                );
        return id;
    }

    public Long replyToProductOrderProposal(Long id, String username, UpdateProductOrderProposalByManagerDto updateProductOrderProposalByManagerDto) {

        //walidacja

        productOrderProposalRepository.findByIdAndManagerUsername(id, username)
                .ifPresentOrElse(
                        productOrderProposal -> {
                            if (!Objects.equals(productOrderProposal.getSide(), ProposalSide.CUSTOMER)) {
                                throw new ValidationException("You cannot reply to your own proposal");
                            }

                            if (!Objects.equals(productOrderProposal.getStatus(), ProposalStatus.PROPOSED)) {
                                throw new ValidationException("Proposal should have status: PROPOSED");
                            }


                            productOrderProposal
                                    .side(ProposalSide.MANAGER)
                                    .discount(Objects.nonNull(updateProductOrderProposalByManagerDto.getDiscount()) ?
                                            updateProductOrderProposalByManagerDto.getDiscount() : productOrderProposal.getDiscount())
                                    .shop(Objects.nonNull(updateProductOrderProposalByManagerDto.getShopName()) ?
                                            shopRepository.findByName(updateProductOrderProposalByManagerDto.getShopName())
                                                    .orElseThrow(() -> new NotFoundException("No shop with name: " + updateProductOrderProposalByManagerDto.getShopName())) :
                                                    productOrderProposal.getShop())
                                    .daysFromOrderToPaymentDeadline(Objects.nonNull(updateProductOrderProposalByManagerDto.getDaysFromOrderToPaymentDeadline()) ?
                                            updateProductOrderProposalByManagerDto.getDaysFromOrderToPaymentDeadline() :
                                            productOrderProposal.getDaysFromOrderToPaymentDeadline());

                        },
                        () -> {
                            throw new NotFoundException("No productOrderProposal with id: " + id + " and for manager username: " + username);
                        }
                );

        return id;

    }

    public Long acceptProductOrderProposal(Long id, String username) {

        productOrderProposalRepository.findByIdAndManagerUsername(id, username)
                .ifPresentOrElse(
                        productOrderProposal -> {
                            if(!isReadyToAccept(productOrderProposal)){
                                throw new ValidationException("");
                            }
                            if (!Objects.equals(productOrderProposal.getSide(), ProposalSide.CUSTOMER)) {
                                throw new ValidationException("You cannot accept your own proposal");
                            }
                            if (!Objects.equals(productOrderProposal.getStatus(), ProposalStatus.PROPOSED)) {
                                throw new ValidationException("Product proposal should have status of: PROPOSED");
                            }

                            productOrderProposal
                                    .side(ProposalSide.MANAGER)
                                    .status(ProposalStatus.ACCEPTED);
                        },
                        () -> {
                            throw new NotFoundException("No productOrderProposal with id: " + id + " and for manager username: " + username);
                        }
                );

        return id;
    }

    private boolean isReadyToAccept(ProductOrderProposal productOrderProposal) {

        Map<String, Object> objectFieldsValues = objectMapper.convertValue(productOrderProposal, new TypeReference<Map<String, Object>>() {
        });

        System.out.println(objectFieldsValues);

        return objectFieldsValues.entrySet()
                .stream()
                .allMatch(e -> Objects.nonNull(e.getValue()));

    }
}
