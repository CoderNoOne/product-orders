//package com.app.application.validators.impl;
//
//import com.app.application.validators.generic.AbstractValidator;
//import com.app.domain.repository.ProducerRepository;
//import com.app.domain.enums.GuaranteeComponent;
//import com.app.infrastructure.dto.createProduct.CreateProductDto;
//import com.app.infrastructure.dto.ProducerDto;
//import lombok.RequiredArgsConstructor;
//import org.apache.logging.log4j.util.Strings;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//@Component
//@RequiredArgsConstructor
//public class ProductValidator extends AbstractValidator<CreateProductDto> {
//
//    private final ProducerValidator producerValidator;
//    private final ProducerRepository producerRepository;
//
//    @Override
//    public Map<String, String> validate(CreateProductDto createProductDto) {
//
//        errors.clear();
//        if (Objects.isNull(createProductDto)) {
//            super.errors.put("product object", "is null");
//            return errors;
//        }
//
//        if (!isProductNameValid(createProductDto.getName())) {
//            errors.put("Name", "Product name is not correct");
//        }
//
//        if (!isCategoryValid(createProductDto.getCategoryName())) {
//            errors.put("Category", "Category is not correct");
//        }
//
//        if (!isPriceValid(createProductDto.getPrice())) {
//            errors.put("Price", "Price is not correct");
//        }
//
//        if (!areGuaranteeComponentsValid(createProductDto.getGuaranteeComponents())) {
//            errors.put("GuaranteeComponents", "GuaranteeComponents are not correct");
//        }
//
////        if (!isProducerValid(createProductDto.getProducerDto())) {
////            errors.put("Producer", "Producer is not correct");
////        }
//
//        return errors;
//    }
//
//    private boolean isCategoryValid(String categoryName) {
//        return Objects.nonNull(categoryName) && Strings.isNotBlank(categoryName);
//    }
//
//    private boolean isPriceValid(BigDecimal price) {
//        return Objects.nonNull(price) && price.compareTo(BigDecimal.ZERO) > 0;
//    }
//
//    private boolean isProductNameValid(String productName) {
//        return Objects.nonNull(productName);
//    }
//
//    private boolean areGuaranteeComponentsValid(List<GuaranteeComponent> guaranteeComponents) {
//        return Objects.nonNull(guaranteeComponents) && guaranteeComponents.stream().noneMatch(Objects::isNull);
//    }
//
//
////    private boolean isProducerValid(ProducerDto producerDto) {
////        boolean initialCheck = Objects.nonNull(producerDto) &&
////                Objects.nonNull(producerDto.getName());
////
////        producerRepository.findByName(producerDto.getName())
////                .ifPresentOrElse(producerFromDb -> {
////                    producerRepository.findGuarantee
////                }, () ->);
////    }
//}
