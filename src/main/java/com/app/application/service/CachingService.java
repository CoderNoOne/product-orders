//package com.app.application.service;
//
//import com.app.domain.entity.AdminShopProperty;
//import com.app.domain.repository.AdminShopPropertyRepository;
//import com.app.domain.entity.ProductOrder;
//import com.app.domain.repository.ProductOrderRepository;
//import com.app.domain.enums.AdminShopPropertyName;
//import com.app.domain.enums.ProductOrderStatus;
//import com.google.common.base.Objects;
//import lombok.RequiredArgsConstructor;
//import org.hibernate.annotations.Cache;
//import org.springframework.cache.annotation.CachePut;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class CachingService {
//
//    private final ProductOrderRepository productOrderRepository;
//
//    @CachePut("deadLines")
//    public Map<String, List<ProductOrder>> getAndCacheProductOrdersWithNoPaymentDoneGroupedByUsername() {
//
//        LocalDate today = LocalDate.now();
//
//        return productOrderRepository.findAll()
//                .stream()
//                .collect(Collectors.groupingBy(
//                        productOrder -> productOrder.getCustomer().getEmail(),
//                        Collectors.filtering(productOrder ->
//                                Objects.equal(productOrder.getStatus(), ProductOrderStatus.IN_PROGRESS) &&
//                                        productOrder.getPaymentDeadline().compareTo(today) > 0, Collectors.toList())));
//    }
//
//    @Cacheable("deadLines")
//    public Map<String, List<ProductOrder>> getProductOrdersWithNoPaymentDoneGroupedByUsername() {
//
//        LocalDate today = LocalDate.now();
//
//        return productOrderRepository.findAll()
//                .stream()
//                .collect(Collectors.groupingBy(
//                        productOrder -> productOrder.getCustomer().getEmail(),
//                        Collectors.filtering(productOrder ->
//                                Objects.equal(productOrder.getStatus(), ProductOrderStatus.IN_PROGRESS) &&
//                                        productOrder.getPaymentDeadline().compareTo(today) > 0, Collectors.toList())));
//    }
//
//
//    @CachePut("warned")
//    public Map<String, List<ProductOrder>> getAndCacheProductOrdersWithWarningGroupedByUsername() {
//
//        LocalDate today = LocalDate.now();
//
//        return productOrderRepository.findAll()
//                .stream()
//                .collect(Collectors.groupingBy(
//                        productOrder -> productOrder.getCustomer().getEmail(),
//                        Collectors.filtering(productOrder ->
//                                Objects.equal(productOrder.getStatus(), ProductOrderStatus.WARNED) &&
//                                        productOrder.getPaymentDeadline().compareTo(today) > 0, Collectors.toList())));
//    }
//
//    @Cacheable("warned")
//    public Map<String, List<ProductOrder>> getProductOrdersWithWarningGroupedByUsername() {
//
//        LocalDate today = LocalDate.now();
//
//        return productOrderRepository.findAll()
//                .stream()
//                .collect(Collectors.groupingBy(
//                        productOrder -> productOrder.getCustomer().getEmail(),
//                        Collectors.filtering(productOrder ->
//                                Objects.equal(productOrder.getStatus(), ProductOrderStatus.WARNED) &&
//                                        productOrder.getPaymentDeadline().compareTo(today) > 0, Collectors.toList())));
//    }
//}
