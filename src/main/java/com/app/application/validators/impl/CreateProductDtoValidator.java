package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.entity.Producer;
import com.app.domain.repository.ProducerRepository;
import com.app.domain.repository.ProductRepository;
import com.app.infrastructure.dto.CreateProducerDto;
import com.app.infrastructure.dto.createProduct.CreateProductDto;
import com.app.infrastructure.dto.GuaranteeDto;
import com.app.infrastructure.dto.ProducerDto;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@SessionScope
public class CreateProductDtoValidator extends AbstractValidator<CreateProductDto> {

    private final ProducerRepository producerRepository;
    private final ProductRepository productRepository;

    @Override
    public Map<String, String> validate(CreateProductDto createProductDto) {

        errors.clear();

        if (Objects.isNull(createProductDto)) {
            super.errors.put("Product object", "is null");
            return errors;
        }

        if (negate(isProductNameValid(createProductDto.getName()))) {
            errors.put("Name", "Product name is not correct");
        }

        if (negate(isCategoryValid(createProductDto.getCategoryName()))) {
            errors.put("Category", "Category is not correct");
        }

        if (negate(isPriceValid(createProductDto.getPrice()))) {
            errors.put("Price", "Price is not correct");
        }

        validateProducerInfo(createProductDto);

        return errors;
    }

    private boolean isProductGuaranteeGuaranteeTimeValid(GuaranteeDto guarantee) {
        return Objects.nonNull(guarantee.getGuaranteeTime()) &&
                (Objects.isNull(guarantee.getGuaranteeTime().getYears()) || guarantee.getGuaranteeTime().getYears() > 0) &&
                (Objects.isNull(guarantee.getGuaranteeTime().getMonths()) || guarantee.getGuaranteeTime().getMonths() > 0) &&
                (Objects.isNull(guarantee.getGuaranteeTime().getDays()) || guarantee.getGuaranteeTime().getDays() > 0) &&

                !(Objects.isNull(guarantee.getGuaranteeTime().getYears()) &&
                        Objects.isNull(guarantee.getGuaranteeTime().getMonths()) &&
                        Objects.isNull(guarantee.getGuaranteeTime().getDays()));
    }


    private void validateProducerInfo(CreateProductDto createProductDto) {

        var producerDto = createProductDto.getProducer();

        if (!isProducerNonNull(producerDto)) {
            errors.put("Producer object", "Producer object is null");
        } else if (!isProducerNameValid(producerDto.getName())) {
            errors.put("Producer name", "Producer name cannot be unspecified");
        } else {

            producerRepository.findByNameWithFetchedGuarantees(producerDto.getName()).ifPresentOrElse(
                    producerFromDb -> {
                        validateProductGuaranteesForExistingProducer(createProductDto, producerFromDb);
                        validateUniqueName(createProductDto.getName(), producerFromDb.getName());
                    },

                    () -> {
                        validateNewProducer(producerDto);
                        validateNewProductAndNewProducerIntegrity(producerDto, createProductDto);
                        validateUniqueName(createProductDto.getName(), producerDto.getName());
                    });
        }
    }

    private void validateUniqueName(String productName, String producerName) {
        if (Objects.nonNull(productName) && productRepository.findByNameAndProducerName(productName, producerName).isPresent()) {
            errors.put("Product name", "There is already a product with name: " + productName + " and producer name: " + producerName);
        }
    }

    private void validateNewProducer(CreateProducerDto producerDto) {

        if (Objects.isNull(producerDto.getTradeName()) || Strings.isBlank(producerDto.getTradeName())) {
            errors.put("Producer trade", "Trade name cannot be null");
        }
    }

    private void validateNewProductAndNewProducerIntegrity(CreateProducerDto producerDto, CreateProductDto createProductDto) {

        boolean yetValidProducerGuarantees = true;

        if (
                Objects.isNull(producerDto.getGuarantees()) ||
                        producerDto.getGuarantees().isEmpty()) {
            errors.put("Producer guarantees", "Producer with that name doesn't exist, so producer guarantees cannot be null or empty");
            yetValidProducerGuarantees = false;
        }

        if (yetValidProducerGuarantees) {
            validateProducerGuarantees(producerDto.getGuarantees());
        }

        if (Objects.isNull(createProductDto.getGuarantee())) {
            errors.put("Product Guarantee", "Product guarantee cannot remain not specified");
        } else if (negate(isProductGuaranteeGuaranteeTimeValid(createProductDto.getGuarantee()))) {
            errors.put("Product guarantee guarantee time", "Product guarantee time is not valid");
        } else if (negate(
                yetValidProducerGuarantees &&
                        producerDto.getGuarantees().contains(createProductDto.getGuarantee())
        )) {
            errors.put("Product guarantee", "Product guarantee should be offered by producer");
        }
    }

    private void validateProducerGuarantees(List<GuaranteeDto> guarantees) {

        if (negate(guarantees.stream().allMatch(this::isSingleGuaranteeNotNull))) {
            errors.put("Producer guarantees", "At least one producer guarantee is null");
            return;
        }

        if (negate(guarantees.stream().allMatch(this::isSingleGuaranteeNameValid))) {
            errors.put("Producer guarantees' name", "At least one producer guarantee name is not valid");
        }

        if (negate(guarantees.stream().allMatch(this::isSingleGuaranteeGuaranteeTimeValid))) {
            errors.put("Producer guarantees' time ", "At least one producer guarantee time (in years) is not valid");
        }

        if (negate(guarantees.stream().allMatch(this::isSingleGuaranteeGuaranteePercentValid))) {
            errors.put("Producer guarantees' percent", "At least one producer guarantee percent is not valid");
        }

        if (negate(guarantees.stream().allMatch(this::isSingleGuaranteeGuaranteeProcessingTimeValid))) {

            errors.put("Producer guarantees' processing time", "At least one producer guarantee processing time is not valid");
        }


        if (!guarantees.stream().allMatch(this::isSingleGuaranteeGuaranteeComponentsValid)) {
            errors.put("Producer guarantees' guarantee components", "At least one producer guarantee component is not valid");
        }

    }

    private boolean isSingleGuaranteeGuaranteeComponentsValid(GuaranteeDto guaranteeDto) {

        return Objects.nonNull(guaranteeDto.getGuaranteeComponents()) &&
                guaranteeDto.getGuaranteeComponents().stream().noneMatch(Objects::isNull);
    }

    private boolean isSingleGuaranteeGuaranteeProcessingTimeValid(GuaranteeDto guaranteeDto) {

        return Objects.nonNull(guaranteeDto) &&
                (Objects.isNull(guaranteeDto.getGuaranteeProcessingTime().getYears()) || guaranteeDto.getGuaranteeProcessingTime().getYears() > 0) &&
                (Objects.isNull(guaranteeDto.getGuaranteeProcessingTime().getMonths()) || guaranteeDto.getGuaranteeProcessingTime().getMonths() > 0) &&
                (Objects.isNull(guaranteeDto.getGuaranteeProcessingTime().getDays()) || guaranteeDto.getGuaranteeProcessingTime().getDays() > 0);

    }

    private boolean isSingleGuaranteeGuaranteePercentValid(GuaranteeDto guaranteeDto) {
        return
                Objects.nonNull(guaranteeDto.getPercent()) &&
                        guaranteeDto.getPercent() > 0 && guaranteeDto.getPercent() < 100;

    }

    private boolean isSingleGuaranteeGuaranteeTimeValid(GuaranteeDto guaranteeDto) {
        return
                Objects.nonNull(guaranteeDto.getGuaranteeTime()) &&
                        (Objects.isNull(guaranteeDto.getGuaranteeTime().getDays()) || guaranteeDto.getGuaranteeTime().getDays() > 0) &&
                        (Objects.isNull(guaranteeDto.getGuaranteeTime().getYears()) || guaranteeDto.getGuaranteeTime().getYears() > 0) &&
                        (Objects.isNull(guaranteeDto.getGuaranteeTime().getMonths()) || guaranteeDto.getGuaranteeTime().getMonths() > 0);


    }

    private boolean isSingleGuaranteeNameValid(GuaranteeDto guaranteeDto) {
        return Objects.nonNull(guaranteeDto.getName()) &&
                Strings.isNotBlank(guaranteeDto.getName());
    }

    private boolean isSingleGuaranteeNotNull(GuaranteeDto guaranteeDto) {
        return Objects.nonNull(guaranteeDto);
    }

    private void validateProductGuaranteesForExistingProducer(CreateProductDto createProductDto, Producer producer) {

        if (
                Objects.isNull(createProductDto.getGuarantee()) ||
                        producer.getGuarantees().stream()
                                .noneMatch(guarantee -> {
                                    var productGuarantee = createProductDto.getGuarantee().toEntity();
                                    return Objects.equals(productGuarantee.getName(), guarantee.getName()) &&
                                            Objects.equals(productGuarantee.getGuaranteeComponents(), guarantee.getGuaranteeComponents()) &&
                                            Objects.equals(productGuarantee.getGuaranteeProcessingTime(), guarantee.getGuaranteeProcessingTime()) &&
                                            Objects.equals(productGuarantee.getGuaranteeTime(), guarantee.getGuaranteeTime()) &&
                                            Objects.equals(productGuarantee.getPercent(), guarantee.getPercent());

                                })) {

            errors.put("Guarantee", "Guarantee should be specified and offered by producer");
        }
    }

    private boolean isProducerNameValid(String name) {

        return
                Objects.nonNull(name) &&
                        Strings.isNotBlank(name);
    }

    private boolean isProducerNonNull(CreateProducerDto producerDto) {
        return Objects.nonNull(producerDto);
    }

    private boolean isCategoryValid(String categoryName) {
        return Objects.nonNull(categoryName) && Strings.isNotBlank(categoryName);
    }

    private boolean isPriceValid(BigDecimal price) {
        return Objects.nonNull(price) && price.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isProductNameValid(String productName) {
        return Objects.nonNull(productName);
    }

    private static boolean negate(boolean logicalValue) {
        return !logicalValue;
    }
}
