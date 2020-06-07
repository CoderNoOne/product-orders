package com.app.application.service;

import com.app.application.mappers.Mappers;
import com.app.application.validators.impl.*;
import com.app.domain.entity.*;
import com.app.domain.enums.*;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.*;
import com.app.infrastructure.dto.*;
import com.app.infrastructure.dto.enums.FilteringKey;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductOrderService {

    private final ProductOrderRepository productOrderRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final ManagerRepository managerRepository;
    private final StockRepository stockRepository;
    private final ComplaintRepository complaintRepository;
    private final ProductRepository productRepository;
    private final CreateProductOrderDtoValidator createProductOrderDtoValidator;
    private final ProductOrderFilteringCriteriaDtoValidator productOrderFilteringCriteriaDtoValidator;
    private final OrderDateBoundaryDtoValidator orderDateBoundaryDtoValidator;
    private final CreateComplaintDtoValidator createComplaintDtoValidator;
    private final AdminShopPropertyRepository adminShopPropertyRepository;
    private final ShopRepository shopRepository;
    private final ReservedProductRepository reservedProductRepository;
    private final ProductOrderProposalRepository productOrderProposalRepository;

    public List<ProductOrderDto> getFilteredProductOrdersForUsername(ProductOrderFilteringCriteriaDto productOrderFilteringCriteriaDto, String username) {

        var errors = productOrderFilteringCriteriaDtoValidator.validate(productOrderFilteringCriteriaDto);
        if (productOrderFilteringCriteriaDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        List<ProductOrder> allByUsername = productOrderRepository.findAllByUsername(username);

        return filter(allByUsername, productOrderFilteringCriteriaDto)
                .stream()
                .filter(productOrder -> productOrder.getStatus() == ProductOrderStatus.DONE)
                .map(ProductOrder::toDto)
                .collect(Collectors.toList());

    }

    private List<ProductOrder> filter(List<ProductOrder> allOrders, ProductOrderFilteringCriteriaDto filteringCriteriaDto) {

        return allOrders.stream()
                .filter(productOrder -> Objects.isNull(filteringCriteriaDto.getFromDate()) || productOrder.getOrderDate().compareTo(filteringCriteriaDto.getFromDate()) >= 0)
                .filter(productOrder -> Objects.isNull(filteringCriteriaDto.getToDate()) || productOrder.getOrderDate().compareTo(filteringCriteriaDto.getToDate()) <= 0)
                .filter(productOrder -> Objects.isNull(filteringCriteriaDto.getMinQuantity()) || productOrder.getQuantity() >= filteringCriteriaDto.getMinQuantity())
                .filter(productOrder -> Objects.isNull(filteringCriteriaDto.getMaxQuantity()) || productOrder.getQuantity() <= filteringCriteriaDto.getMaxQuantity())
                .filter(productOrder -> Objects.isNull(filteringCriteriaDto.getCategory()) || productOrder.getProduct().getCategory().getName().equals(filteringCriteriaDto.getCategory()))
                .filter(productOrder -> Objects.isNull(filteringCriteriaDto.getProductName()) || productOrder.getProduct().getName().equals(filteringCriteriaDto.getProducerName()))
                .filter(productOrder -> Objects.isNull(filteringCriteriaDto.getProducerName()) || productOrder.getProduct().getProducer().getName().equals(filteringCriteriaDto.getProducerName()))
                .filter(productOrder -> Objects.isNull(filteringCriteriaDto.getMaxPrice()) || productOrder.getProduct().getPrice().compareTo(filteringCriteriaDto.getMaxPrice()) <= 0)
                .filter(productOrder -> Objects.isNull(filteringCriteriaDto.getMinPrice()) || productOrder.getProduct().getPrice().compareTo(filteringCriteriaDto.getMinPrice()) >= 0)
                .collect(Collectors.toList());

    }


    public List<ProductOrderDto> getFilteredProductOrdersByKeyWordForUsername(KeywordDto keywordDto, String username) {

        if (Objects.isNull(keywordDto)) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("KeywordDto object", "is null")));
        } else if (Objects.isNull(keywordDto.getWord())) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("Key word", "is null")));
        } else if (Strings.isBlank(keywordDto.getWord())) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("Key word", "is blank")));
        }

        List<ProductOrder> allByUsername = productOrderRepository.findAllByUsername(username);

        return allByUsername.stream()
                .filter(productOrder ->
                        Objects.equals(productOrder.getProduct().getName(), keywordDto.getWord()) ||
                                Objects.equals(productOrder.getOrderDate().toString(), keywordDto.getWord()) ||
                                Objects.equals(productOrder.getDeliveryAddress().getAddress(), keywordDto.getWord()) ||
                                Objects.equals(productOrder.getDiscount().toString(), keywordDto.getWord()) ||
                                Objects.equals(productOrder.getPaymentDeadline().toString(), keywordDto.getWord()) ||
                                Objects.equals(productOrder.getStatus().toString(), keywordDto.getWord()) ||
                                Objects.equals(productOrder.getQuantity().toString(), keywordDto.getWord())
                )
                .map(ProductOrder::toDto)
                .collect(Collectors.toList());
    }

    public Long addComplaint(Long id, String username, CreateComplaintDto createComplaintDto) {

        var errors = createComplaintDtoValidator.validate(createComplaintDto);
        if (createComplaintDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var idWrapper = new AtomicLong();

        productOrderRepository.findByIdAndCustomerUsername(id, username).ifPresentOrElse(
                productOrder ->
                        idWrapper.set(complaintRepository.save(Complaint.builder()
                                .damageType(DamageType.valueOf(createComplaintDto.getDamageType()))
                                .issueDate(LocalDate.now())
                                .productOrder(productOrder)
                                .status(ComplaintStatus.REQUESTED)
                                .build()).getId())
                ,
                () -> {
                    throw new NotFoundException("No productOrder with id: " + id);
                }
        );

        return idWrapper.get();
    }

    public void deleteById(Long id, String username) {

        if (Objects.isNull(id)) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("Id", "is null")));
        }

        var productOrder = productOrderRepository.findByIdAndCustomerUsername(id, username)
                .orElseThrow(() -> new NotFoundException("No productOrder with id: " + id));


        if (!productOrder.getStatus().equals(ProductOrderStatus.IN_PROGRESS)) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("Product order status", "Order need to have status in_progress to be cancelled")));
        }

        var reservedProducts = reservedProductRepository.findAllByProductOrderId(productOrder.getId());
        reservedProducts.forEach(reservedProduct -> {
            stockRepository.findOne(reservedProduct.getStock().getId())
                    .ifPresent(stock -> stock.getProductsQuantity().merge(productOrder.getProduct(), reservedProduct.getQuantity(), Integer::sum));
        });

        reservedProductRepository.deleteByProductOrderId(productOrder.getId());
        productOrderRepository.deleteById(productOrder.getId());

    }

    public Long issueAnInvoice(Long id, String username) {

        if (Objects.isNull(id)) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("Id", "is null")));
        }

        productOrderRepository.findByIdAndCustomerUsername(id, username)
                .ifPresentOrElse(
                        productOrder -> {
                            if (!Objects.equals(productOrder.getStatus(), ProductOrderStatus.DONE)) {
                                throw new ValidationException(Validations.createErrorMessage(Map.of("Product order status", "must be DONE")));
                            }


                        },
                        () -> {
                            throw new NotFoundException("No product order with id: " + id);
                        }
                );


        return id;
    }

    public Long makePurchaseForOrderByIdAndUsername(Long id, String username) {

        if (Objects.isNull(id)) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("ProductOrder id", "is null")));
        }

        productOrderRepository.findByIdAndCustomerUsername(id, username)
                .ifPresentOrElse(
                        productOrder -> {
                            if (Objects.equals(productOrder.getStatus(), ProductOrderStatus.DONE)) {
                                throw new ValidationException(
                                        Validations.createErrorMessage(
                                                Map.of("Payment done", "You have already paid for this order")));
                            }
                            productOrder.setStatus(ProductOrderStatus.DONE);
                            var shop = productOrder.getShop();
                            var augend = calculateTotalPriceToPay(productOrder);
                            System.out.println(augend);
                            shop.setBudget(shop.getBudget().add(augend));
                            reservedProductRepository.deleteByProductOrderId(productOrder.getId());
                        },
                        () -> {
                            throw new NotFoundException("No productOrder with id: " + id);
                        }
                );

        return id;
    }

    private BigDecimal calculateTotalPriceToPay(ProductOrder productOrder) {

        var quantity = productOrder.getQuantity();
        var penalty = productOrder.getPenalty();
        var productBasePrice = productOrder.getProduct().getPrice();
        var discount = productOrder.getDiscount();

        var pricePercentageAfterDiscount = BigDecimal.ONE.subtract(discount.divide(new BigDecimal("100"), RoundingMode.HALF_UP));
        return Objects.isNull(penalty) ?
                pricePercentageAfterDiscount.multiply(productBasePrice.multiply(new BigDecimal(quantity))) :
                (BigDecimal.ONE.add(penalty.divide(new BigDecimal("100"), RoundingMode.HALF_UP))).multiply(pricePercentageAfterDiscount).multiply(productBasePrice.multiply(new BigDecimal(quantity)));
    }


    public Map<String, BigDecimal> groupPriceByKey(String key, String username) {

        if (Objects.isNull(key)) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("Filtering key", "is null")));
        } else if (Arrays.stream(FilteringKey.values()).map(FilteringKey::name).noneMatch(filteringKey -> Objects.equals(filteringKey, key))) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("Filtering key", "is not valid")));
        }
        var productOrders = productOrderRepository.findAllByUsernameAndStatus(username, ProductOrderStatus.DONE);

        return switch (FilteringKey.valueOf(key)) {
            case CATEGORY -> groupByCategory(productOrders);
            case PRODUCER -> groupByProducer(productOrders);
        };
    }

    private Map<String, BigDecimal> groupByProducer(List<ProductOrder> productOrders) {

        return productOrders
                .stream()
                .filter(productOrder -> productOrder.getStatus() == ProductOrderStatus.DONE)
                .collect(Collectors.groupingBy(productOrder -> productOrder.getProduct().getProducer().getName(),
                        Collectors.mapping(productOrder -> productOrder.getProduct().getPrice().multiply(BigDecimal.valueOf(productOrder.getQuantity()).multiply(BigDecimal.valueOf(100).subtract(productOrder.getDiscount()))),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
    }

    private Map<String, BigDecimal> groupByCategory(List<ProductOrder> productOrders) {

        return productOrders.stream()
                .filter(productOrder -> Objects.equals(productOrder.getStatus(), ProductOrderStatus.DONE))
                .collect(Collectors.groupingBy(productOrder -> productOrder.getProduct().getCategory().getName(),
                        Collectors.mapping(productOrder -> productOrder.getProduct().getPrice().multiply(BigDecimal.valueOf(productOrder.getQuantity()).multiply(BigDecimal.valueOf(100).subtract(productOrder.getDiscount()))),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
    }

    public BigDecimal getTotalPriceByOrderDateForUsername(OrderDateBoundaryDto orderDateBoundary, String username) {

        var errors = orderDateBoundaryDtoValidator.validate(orderDateBoundary);
        if (orderDateBoundaryDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        return productOrderRepository.findAllByUsernameAndStatus(username, ProductOrderStatus.DONE)
                .stream()
                .filter(productOrder -> productOrder.getStatus() == ProductOrderStatus.DONE)
                .filter(productOrder -> Objects.isNull(orderDateBoundary.getFrom()) || orderDateBoundary.getFrom().compareTo(productOrder.getOrderDate()) <= 0)
                .filter(productOrder -> Objects.isNull(orderDateBoundary.getTo()) || orderDateBoundary.getTo().compareTo(productOrder.getOrderDate()) >= 0)
                .map(productOrder -> productOrder.getProduct().getPrice().multiply(BigDecimal.valueOf(productOrder.getQuantity()).multiply(BigDecimal.valueOf(100).subtract(productOrder.getDiscount()))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public void changeStatusToWarnedAndMovePaymentDeadlineByDays(List<Long> productOrdersIds, Integer days) {

        Optional<AdminShopProperty> adminProperty = adminShopPropertyRepository.findByProperty(AdminShopPropertyName.P);

        productOrderRepository.findAllByIdIn(productOrdersIds)
                .forEach((productOrder -> {
                    productOrder.setStatus(ProductOrderStatus.WARNED);
                    productOrder.setPaymentDeadline(productOrder.getPaymentDeadline().plusDays(days));
//                    productOrder.setPrice(adminProperty.isPresent() ? productOrder.getPrice().add(productOrder.getPrice().multiply(adminProperty.get().getValue())) : productOrder.getPrice());
                }));

//        productOrderRepository.saveAll(changedProductOrders);

    }

    public Map<CustomerEmailAndUsername, List<ProductOrderDto>> getProductOrdersWithNoPaymentDoneGroupedByUser() {

        LocalDate today = LocalDate.now();

        return productOrderRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        productOrder -> Mappers.fromCustomerToCustomerEmailAndUsername(productOrder.getCustomer()),
                        Collectors.mapping(ProductOrder::toDto, Collectors.filtering(productOrderDto ->
                                Objects.equals(productOrderDto.getStatus(), ProductOrderStatus.IN_PROGRESS) &&
                                        productOrderDto.getPaymentDeadline().compareTo(today) > 0, Collectors.toList()))));

    }

    public Map<CustomerEmailAndUsername, List<ProductOrderDto>> getProductOrdersWithWarnedStatusGroupByUser() {

        LocalDate today = LocalDate.now();

        return productOrderRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        productOrder -> Mappers.fromCustomerToCustomerEmailAndUsername(productOrder.getCustomer()),
                        Collectors.mapping(ProductOrder::toDto, Collectors.filtering(productOrderDto ->
                                Objects.equals(productOrderDto.getStatus(), ProductOrderStatus.WARNED) &&
                                        productOrderDto.getPaymentDeadline().compareTo(today) > 0, Collectors.toList()))));
    }

    public Map<CustomerEmailAndUsername, List<ProductOrderDto>> getProductOrdersToSuspend() {

        LocalDate today = LocalDate.now();

        return productOrderRepository.findAll()
                .stream()
                .filter(productOrder ->
                        Objects.equals(productOrder.getStatus(), ProductOrderStatus.WARNED) &&
                                productOrder.getPaymentDeadline().compareTo(today) < 0)
                .collect(Collectors.groupingBy(
                        productOrder -> Mappers.fromCustomerToCustomerEmailAndUsername(productOrder.getCustomer()),
                        Collectors.mapping(ProductOrder::toDto, Collectors.toList())));

    }

    public void changeOrderStatusToSuspend(List<Long> productOrdersIds) {

        productOrderRepository.findAllByIdIn(productOrdersIds)
                .forEach(
                        productOrder -> productOrder.setStatus(ProductOrderStatus.SUSPENDED)
                );
    }

    public ProductOrderDto getById(Long id) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("ProductOrder is null");
        }

        return productOrderRepository.findOne(id)
                .map(ProductOrder::toDto)
                .orElseThrow(() -> new NotFoundException("No productOrder with id: " + id));
    }

    public List<ProductOrderDto> getAllProductOrdersForUsername(String username) {
        return productOrderRepository.findAllByUsername(username)
                .stream()
                .map(ProductOrder::toDto)
                .collect(Collectors.toList());
    }

    public Long addProductOrder(String managerUsername, CreateProductOrderDto createProductOrderDto) {

        if (Objects.nonNull(createProductOrderDto)) {
            createProductOrderDto.setManagerUsername(managerUsername);
        }

        var errors = createProductOrderDtoValidator.validate(createProductOrderDto);

        if (createProductOrderDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var productStockQuantity = createProductOrderDto.getProductStockQuantity().entrySet()
                .stream()
                .collect(Collectors.toMap(e -> Long.valueOf(e.getKey()), Map.Entry::getValue));

        ProductOrderProposal productOrderProposal = productOrderProposalRepository.findByIdAndManagerUsernameAndStatus(createProductOrderDto.getAcceptedProductOrderProposalId(), managerUsername, ProposalStatus.ACCEPTED)
                .orElseThrow(() -> new NotFoundException("No productOrder proposal with specified details found"));

        Product product = productOrderProposal.getProduct();


        var stocks = stockRepository.findAllByIdIn(productStockQuantity.keySet());

        var totalQuantity = new AtomicInteger(0);

        List<ReservedProduct> reservedProductsToSave = new ArrayList<>();
        stocks.forEach(stock -> {

            if (!stock.getProductsQuantity().containsKey(product) || stock.getProductsQuantity().get(product) < productStockQuantity.get(stock.getId())) {
                throw new ValidationException("No enough product in stock: " + stock.getId());
            }

            totalQuantity.addAndGet(productStockQuantity.get(stock.getId()));
            stock.getProductsQuantity().merge(product, -productStockQuantity.get(stock.getId()), Integer::sum);

            if(totalQuantity.get() != productOrderProposal.getQuantity()){
                throw new ValidationException("You ordered: " + totalQuantity.get() + " but your client wanted: " + productOrderProposal.getQuantity());
            }

            reservedProductsToSave.add(ReservedProduct.builder()
                    .stock(stock)
                    .quantity(productStockQuantity.get(stock.getId()))
                    .build());

            if (stock.getProductsQuantity().get(product).equals(0)) {
                stock.getProductsQuantity().remove(product);
            }
        });

        ProductOrder productOrderToSave = ProductOrder.builder()
                .customer(productOrderProposal.getCustomer())
                .product(product)
                .shop(productOrderProposal.getShop())
                .deliveryAddress(productOrderProposal.getDeliveryAddress())
                .orderDate(LocalDate.now())
                .paymentDeadline(LocalDate.now().plusDays(productOrderProposal.getDaysFromOrderToPaymentDeadline()))
                .quantity(totalQuantity.get())
                .status(ProductOrderStatus.DONE)
                .build();


        productOrderProposal.setStatus(ProposalStatus.DONE);

        var savedProductOrder = productOrderRepository.save(productOrderToSave);

        reservedProductsToSave.forEach(reservedProduct -> reservedProduct.setProductOrder(savedProductOrder));

        reservedProductRepository.saveAll(reservedProductsToSave);

        return savedProductOrder.getId();
    }


//    public Long addProductOrder(String managerUsername, CreateProductOrderDto createProductOrderDto) {
//
//        var errors = createProductOrderDtoValidator.validate(createProductOrderDto);
//
//        if (createProductOrderDtoValidator.hasErrors()) {
//            throw new ValidationException(Validations.createErrorMessage(errors));
//        }
//
//        var productStockQuantity = createProductOrderDto.getProductStockQuantity().entrySet()
//                .stream()
//                .collect(Collectors.toMap(e -> Long.valueOf(e.getKey()), Map.Entry::getValue));
//
//        var product = productRepository.findOne(createProductOrderDto.getProductId())
//                .orElseThrow(() -> new NotFoundException("No product with id: " + createProductOrderDto.getProductId()));
//
//        var customer = customerRepository
//                .findByUsername(createProductOrderDto.getCustomerUsername())
//                .orElseThrow(() -> new NotFoundException("No customer with username: " + createProductOrderDto.getCustomerUsername()));
//
//        var shop = shopRepository.findOne(createProductOrderDto.getShopId())
//                .orElseThrow(() -> new NotFoundException("No shop with id: " + createProductOrderDto.getShopId()));
//
//        if (!Objects.equals(customer.getManager().getUsername(), managerUsername)) {
//            throw new ValidationException("You are not the manager of customer with username: " + createProductOrderDto.getCustomerUsername());
//        }
//
//
//        var stocks = stockRepository.findAllByIdIn(productStockQuantity.keySet());
//
//        var totalQuantity = new AtomicInteger(0);
//
//        List<ReservedProduct> reservedProductsToSave = new ArrayList<>();
//        stocks.forEach(stock -> {
//
//            if (!stock.getProductsQuantity().containsKey(product) || stock.getProductsQuantity().get(product) < productStockQuantity.get(stock.getId())) {
//                throw new ValidationException("No enough product in stock: " + stock.getId());
//            }
//
//            totalQuantity.addAndGet(productStockQuantity.get(stock.getId()));
//            stock.getProductsQuantity().merge(product, -productStockQuantity.get(stock.getId()), Integer::sum);
//
//            reservedProductsToSave.add(ReservedProduct.builder()
//                    .stock(stock)
//                    .quantity(productStockQuantity.get(stock.getId()))
//                    .build());
//
//            if (stock.getProductsQuantity().get(product).equals(0)) {
//                stock.getProductsQuantity().remove(product);
//            }
//        });
//
//        var productOrderToSave = createProductOrderDto
//                .toEntity()
//                .customer(customer)
//                .product(product)
//                .orderDate(LocalDate.now())
//                .quantity(totalQuantity.get())
//                .deliveryAddress(addressRepository.findByAddress(createProductOrderDto.getDeliveryAddress())
//                        .orElseGet(() -> addressRepository.save(
//                                Address.builder()
//                                        .address(createProductOrderDto
//                                                .getDeliveryAddress())
//                                        .build())))
//                .shop(shop);
//
//        var savedProductOrder = productOrderRepository.save(productOrderToSave);
//
//        reservedProductsToSave.forEach(reservedProduct -> reservedProduct.setProductOrder(savedProductOrder));
//
//        reservedProductRepository.saveAll(reservedProductsToSave);
//
//        return savedProductOrder.getId();
//    }
}
