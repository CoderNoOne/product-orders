package com.app.application.service;

import com.app.application.mappers.Mappers;
import com.app.application.validators.impl.CreateComplaintDtoValidator;
import com.app.application.validators.impl.CreateProductOrderDtoValidator;
import com.app.application.validators.impl.OrderDateBoundaryDtoValidator;
import com.app.application.validators.impl.ProductOrderFilteringCriteriaDtoValidator;
import com.app.domain.entity.*;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.domain.enums.ComplaintStatus;
import com.app.domain.enums.DamageType;
import com.app.domain.enums.ProductOrderStatus;
import com.app.domain.entity.Product;
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
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
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


//    public Long addProductOrder(String username, CreateProductOrderDto createProductOrderDto) {
//
//        var errors = createProductOrderDtoValidator.validate(createProductOrderDto);
//        if (createProductOrderDtoValidator.hasErrors()) {
//            throw new ValidationException(Validations.createErrorMessage(errors));
//        }
//
//        Product product = productRepository
//                .findByNameAndProducerName(createProductOrderDto.getProductInfo().getName(),
//                        createProductOrderDto.getProductInfo().getProducerName())
//                .orElseThrow(() -> new NotFoundException(MessageFormat.format("No product with (name : {0}, producerName: {1})",
//                        createProductOrderDto.getProductInfo().getName(),
//                        createProductOrderDto.getProductInfo().getProducerName())));
//
//        Map<Product, Integer> productsQuantity = stockRepository.findOne(createProductOrderDto.getStockId())
//                .orElseThrow(() -> new NotFoundException("No stock with id: " + createProductOrderDto.getStockId()))
//                .getProductsQuantity();
//
//        ProductOrder productOrder = createProductOrderDto.toEntity();
//        productOrder.setProduct(product);
//        productOrder.setOrderDate(LocalDate.now());
//        productOrder.setPaymentDeadline(LocalDate.now().plusDays(7));
//
//        addressRepository.findByAddress(productOrder.getDeliveryAddress().getAddress())
//                .ifPresentOrElse(productOrder::setDeliveryAddress,
//                        () -> {
//                            Address savedAddress = addressRepository.save(productOrder.getDeliveryAddress());
//                            productOrder.setDeliveryAddress(savedAddress);
//                        });
//
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new NotFoundException("No customer with username: " + username));
//
//        productOrder.setDiscount(calculateDiscount(product, productOrder.getQuantity(), customer));
//
//        productOrder.setCustomer(customer);
//
//        productsQuantity.merge(
//                product, createProductOrderDto.getQuantity(), (oldVal, newVal) -> oldVal - newVal);
//
//        return productOrderRepository.save(productOrder).
//                getId();
//
//    }

//    private BigDecimal calculateDiscount(Product product, Integer quantity, Customer customer) {
//
//        var totalPrice = calculateTotalPrice(quantity, product.getPrice());
//
//        BigDecimal discount = new BigDecimal(0);
//
//        if (customer.getAge() <= 25) {
//            discount = discount.add(BigDecimal.valueOf(5));
//        }
//        if (totalPrice.compareTo(BigDecimal.valueOf(500)) >= 0) {
//            discount = discount.add(BigDecimal.TEN);
//        }
//
//        if (productOrderRepository.findByUsernameAndProducerName(customer.getUsername(), product.getProducer().getName()).size() >= 50) {
//            discount = discount.add(BigDecimal.valueOf(5));
//        }
//
//        return discount;
//    }

//    private BigDecimal calculateTotalPrice(Integer quantity, BigDecimal price) {
//        return new BigDecimal(String.valueOf(quantity)).multiply(price);
//    }

    public List<ProductOrderDto> getFilteredProductOrdersForUsername(ProductOrderFilteringCriteriaDto productOrderFilteringCriteriaDto, String username) {

        var errors = productOrderFilteringCriteriaDtoValidator.validate(productOrderFilteringCriteriaDto);
        if (productOrderFilteringCriteriaDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        List<ProductOrder> allByUsername = productOrderRepository.findAllByUsername(username);

        return filter(allByUsername, productOrderFilteringCriteriaDto)
                .stream()
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
                                .status(ComplaintStatus.AWAITING)
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

        productOrderRepository.findByIdAndCustomerUsername(id, username)
                .ifPresentOrElse(
                        productOrder -> {
                            if (productOrder.getStatus().equals(ProductOrderStatus.DONE)) {
                                throw new ValidationException(Validations.createErrorMessage(Map.of("Product order status", "Order is already done. Cannot be canceled")));
                            }
                            productOrderRepository.delete(productOrder);
                        },
                        () -> {
                            throw new NotFoundException("No productOrder with id: " + id);
                        }
                );

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
                                throw new ValidationException(Validations.createErrorMessage(Map.of("Payment done", "You have arleady paid for this order")));
                            }
                            productOrder.setStatus(ProductOrderStatus.DONE);
                        },
                        () -> {
                            throw new NotFoundException("No productOrder with id: " + id);
                        }
                );

        return id;
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

    public Long addProductOrder(String managerUsername, CreateProductOrderDto2 createProductOrderDto) {

        var productStockQuantity = createProductOrderDto.getProductStockQuantity().entrySet()
                .stream()
                .collect(Collectors.toMap(e -> Long.valueOf(e.getKey()), Map.Entry::getValue));

        var product = productRepository.findOne(createProductOrderDto.getProductId())
                .orElseThrow(() -> new NotFoundException("No product with id: " + createProductOrderDto.getProductId()));

        var customer = customerRepository
                .findByUsername(createProductOrderDto.getCustomerUsername())
                .orElseThrow(() -> new NotFoundException("No customer with username: " + createProductOrderDto.getCustomerUsername()));

        var shop = shopRepository.findOne(createProductOrderDto.getShopId())
                .orElseThrow(() -> new NotFoundException("No shop with id: " + createProductOrderDto.getShopId()));

        if (!Objects.equals(customer.getManager().getUsername(), managerUsername)) {
            throw new ValidationException("You are not the manager of customer with username: " + createProductOrderDto.getCustomerUsername());
        }

        var stocks = stockRepository.findAllByIdIn(productStockQuantity.keySet());

        var totalQuantity = new AtomicInteger(0);

        stocks.forEach(stock -> {
            if (stock.getProductsQuantity().get(product) < productStockQuantity.get(stock.getId())) {
                throw new ValidationException("No enough product in stock: " + stock.getId());
            }
            totalQuantity.addAndGet(productStockQuantity.get(stock.getId()));
            stock.getProductsQuantity().merge(product, -productStockQuantity.get(stock.getId()), Integer::sum);

            if (stock.getProductsQuantity().get(product).equals(0)) {
                stock.getProductsQuantity().remove(product);
            }
        });

        var productOrderToSave = createProductOrderDto
                .toEntity()
                .customer(customer)
                .product(product)
                .orderDate(LocalDate.now())
                .quantity(totalQuantity.get())
                .deliveryAddress(addressRepository.findByAddress(createProductOrderDto.getDeliveryAddress())
                        .orElseGet(() -> addressRepository.save(
                                Address.builder()
                                        .address(createProductOrderDto
                                                .getDeliveryAddress())
                                        .build())))
                .shop(shop);

        return productOrderRepository.save(productOrderToSave).getId();
    }
}
