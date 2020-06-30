package com.app.application.service;

import com.app.application.validators.impl.AddProductToStockDtoValidator;
import com.app.application.validators.impl.ProductQuantityDtoValidator;
import com.app.application.validators.impl.TransferProductDtoValidator;
import com.app.domain.entity.Product;
import com.app.domain.entity.Stock;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.domain.repository.AdminShopPropertyRepository;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.dto.AddProductToStockDto;
import com.app.infrastructure.dto.TransferProductDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import com.app.infrastructure.dto.createShop.ProductQuantityDto;
import com.app.infrastructure.exception.AdminPropertyNotFoundException;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.NullIdValueException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final ProductQuantityDtoValidator productQuantityDtoValidator;
    private final TransferProductDtoValidator transferProductDtoValidator;
    private final AddProductToStockDtoValidator addProductToStockDtoValidator;
    private final AdminShopPropertyRepository adminShopPropertyRepository;

    public Long addProductToStock(AddProductToStockDto addProductToStockDto) {

        var errors = addProductToStockDtoValidator.validate(addProductToStockDto);

        if (addProductToStockDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        Stock stock = stockRepository.findOne(addProductToStockDto.getStockId())
                .orElseThrow(() -> new NotFoundException("No stock with id: " + addProductToStockDto.getStockId()));

        Product product = productRepository.findByNameAndProducerName(addProductToStockDto.getProductInfo().getName(), addProductToStockDto.getProductInfo().getProducerName())
                .orElseThrow(() -> new NotFoundException(MessageFormat.format(
                        "No product with (name: {0}, producerName: {1})",
                        addProductToStockDto.getProductInfo().getName(),
                        addProductToStockDto.getProductInfo().getProducerName()
                )));


        BigDecimal xValue = adminShopPropertyRepository.findByProperty(AdminShopPropertyName.X)
                .orElseThrow(() -> new AdminPropertyNotFoundException("No value for X property found. Cannot add product. Try again later"))
                .getValue();

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(addProductToStockDto.getQuantity()));

        var shop = stock.getShop();
        var budget = shop.getBudget();

        if (totalPrice.compareTo(budget.multiply(xValue)) > 0) {
            throw new ValidationException("Cannot add products to stock - budget limit");
        }

        shop.setBudget(budget.subtract(totalPrice));

        shop.setBudget(budget.subtract(totalPrice));
        stock.getProductsQuantity().merge(product, addProductToStockDto.getQuantity(), Integer::sum);

        return product.getId();
    }

    public List<ProductQuantityDto> getAllProductsForStock(Long id) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Stock id is null");
        }

        var productsQuantityList = new AtomicReference<List<ProductQuantityDto>>();

        stockRepository.findOne(id)
                .ifPresentOrElse(
                        stockFromDb -> {
                            productsQuantityList.set(stockFromDb.getProductsQuantity().entrySet()
                                    .stream()
                                    .map(e -> ProductQuantityDto.builder()
                                            .productInfo(ProductInfo.builder()
                                                    .name(e.getKey().getName())
                                                    .producerName(e.getKey().getProducer().getName())
                                                    .build())
                                            .quantity(e.getValue())
                                            .build())
                                    .collect(Collectors.toList()));
                        },
                        () -> {
                            throw new NotFoundException("No stock with id: " + id);
                        }
                );

        return productsQuantityList.get();
    }

    public Long relocateProducts(TransferProductDto transferProductDto) {

        var errors = transferProductDtoValidator.validate(transferProductDto);

        if (transferProductDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var productIdWrapper = new AtomicLong();
        stockRepository.findOne(transferProductDto.getStockFrom())
                .ifPresentOrElse(stockFrom -> {
                    Product product = productRepository.findByNameAndProducerName(transferProductDto.getProductInfo().getName(), transferProductDto.getProductInfo().getProducerName())
                            .orElseThrow(() -> new ValidationException("No product"));
                    productIdWrapper.set(product.getId());
                    stockFrom.getProductsQuantity().merge(product, transferProductDto.getQuantity(), (oldVal, newVal) -> oldVal - newVal);
                    if(stockFrom.getProductsQuantity().get(product) == 0){
                        stockFrom.getProductsQuantity().remove(product);
                    }

                    Stock stockTo = stockRepository.findOne(transferProductDto.getStockTo()).orElseThrow(() -> new ValidationException("No stockTo"));
                    stockTo.getProductsQuantity().merge(product, transferProductDto.getQuantity(), Integer::sum);
                }, () -> {
                    throw new ValidationException("No stock from");
                });


        return productIdWrapper.get();
    }

}
