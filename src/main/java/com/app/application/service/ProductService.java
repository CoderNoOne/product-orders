package com.app.application.service;

import com.app.application.validators.impl.CreateProductDtoValidator;
import com.app.application.validators.impl.UpdateProductDtoValidator;
import com.app.domain.entity.Category;
import com.app.domain.entity.Producer;
import com.app.domain.entity.Trade;
import com.app.domain.entity.Product;
import com.app.domain.repository.*;
import com.app.infrastructure.dto.createProduct.CreateProductDto;
import com.app.infrastructure.dto.ProductDto;
import com.app.infrastructure.dto.UpdateProductDto;
import com.app.infrastructure.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProducerRepository producerRepository;
    private final TradeRepository tradeRepository;
    private final CreateProductDtoValidator createProductDtoValidator;
    private final GuaranteeRepository guaranteeRepository;
    private final UpdateProductDtoValidator updateProductDtoValidator;
    private final ProductOrderRepository productOrderRepository;
    private final StockRepository stockRepository;

    public List<ProductDto> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(Product::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto getById(Long id) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Product id is null");
        }

        return productRepository.findOne(id)
                .orElseThrow(() -> new NotFoundException(MessageFormat.format("No product with id {0}", id)))
                .toDto();
    }

    public Long addProduct(CreateProductDto createProductDto) {
        var errors = createProductDtoValidator.validate(createProductDto);

        if (createProductDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        Product product = createProductDto.toEntity();

        AtomicLong savedProductIdWrapper = new AtomicLong();
        producerRepository.findByName(createProductDto.getProducer().getName())
                .ifPresentOrElse(
                        producerFromDb -> savedProductIdWrapper.set(saveProduct(producerFromDb, createProductDto.toEntity())),
                        () -> {
                            var trade = setProducerTrade(product.getProducer(), createProductDto.getProducer().getTradeName());
                            setGuarantees(product.getProducer());

                            Producer savedProducer = producerRepository.save(product.getProducer());
                            trade.getProducers().add(savedProducer);

                            savedProductIdWrapper.set(saveProduct(product.getProducer(), product));
                        }
                );

        return savedProductIdWrapper.get();

    }

    private void setGuarantees(Producer producer) {

        producer.getGuarantees().forEach(guarantee -> guarantee.setProducer(producer));
        var savedGuarantees = guaranteeRepository.saveAll(producer.getGuarantees());
        producer.setGuarantees(savedGuarantees);
    }

    private Long saveProduct(Producer producerFromDb, Product product) {
        product.setProducer(producerFromDb);
        var category = setProductCategory(product);
        var guaranteeName = product.getGuarantee().getName();
        guaranteeRepository.findByName(guaranteeName).ifPresent(product::setGuarantee);

        var savedProduct = productRepository.save(product);
        producerFromDb.getProducts().add(savedProduct);
        category.getProducts().add(savedProduct);
        return savedProduct.getId();
    }

    private Trade setProducerTrade(Producer producer, String tradeName) {

        AtomicReference<Trade> tradeWrapper = new AtomicReference<>();
        tradeRepository.findByName(tradeName)
                .ifPresentOrElse(tradeFromDb -> {
                            producer.setTrade(tradeFromDb);
                            tradeWrapper.set(tradeFromDb);
                        },
                        () -> {
                            Trade savedTrade = tradeRepository.save(Trade.builder()
                                    .name(tradeName)
                                    .producers(new ArrayList<>())
                                    .build());
                            tradeWrapper.set(savedTrade);
                            producer.setTrade(savedTrade);
                        });

        return tradeWrapper.get();
    }

    private Category setProductCategory(Product product) {

        String categoryName = product.getCategory().getName();

        AtomicReference<Category> categoryWrapper = new AtomicReference<>();
        categoryRepository.findByName(categoryName)
                .ifPresentOrElse(categoryFromDb -> {
                            product.setCategory(categoryFromDb);
                            categoryWrapper.set(categoryFromDb);
                        },
                        () -> {
                            Category savedCategory = categoryRepository.save(Category.builder()
                                    .name(categoryName)
                                    .products(new ArrayList<>())
                                    .build());

                            categoryWrapper.set(savedCategory);
                            product.setCategory(savedCategory);
                        });

        return categoryWrapper.get();
    }

    public void delete(Long id) {

        if (Objects.isNull(id)) {
            throw new NullReferenceException("Product id is null");
        }

        productRepository.findOne(id)
                .ifPresentOrElse(product -> {

                            if (stockRepository.doProductExistsInAnyStock(product.getId())) {
                                throw new IllegalStateException("Cannot delete a product. It exists in stock");
                            }
                            if (productOrderRepository.hasProductBeenOrdered(product.getId())) {
                                throw new IllegalStateException("Cannot delete a product. It has been ordered already");
                            }

                            productRepository.delete(product);
                        },
                        () -> {
                            throw new NotFoundException(MessageFormat.format("No product with id {0}", id));
                        }
                );

    }

    public Long updateProduct(Long id, UpdateProductDto updateProductDto) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Product id is null");
        }

        var errors = updateProductDtoValidator.validate(updateProductDto);

        if (updateProductDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var productIdWrapper = new AtomicLong();

        productRepository.findOne(id)
                .ifPresentOrElse(product -> {
                    productIdWrapper.set(product.getId());
                    product.setPrice(Objects.nonNull(updateProductDto.getPrice()) ? updateProductDto.getPrice() : product.getPrice());

                    if (Objects.nonNull(updateProductDto.getName()) && productRepository.findByNameAndProducerName(updateProductDto.getName(), product.getProducer().getName()).isPresent()) {
                        throw new ValidationException(Validations.createErrorMessage(Map.of("Product name", "There is already a product with provided name for that producer. It has to be unique")));
                    } else if (Objects.nonNull(updateProductDto.getName())) {
                        product.setName(updateProductDto.getName());
                    }
                }, () -> {
                    throw new NotFoundException(MessageFormat.format("No product with id {0}", id));
                });

        return productIdWrapper.get();
    }

    public List<ProductDto> getProductsByCategory(String category) {

        if (Objects.isNull(category)) {
            throw new NullReferenceException("Category is null");
        }

        return productRepository.findAllByCategory(category)
                .stream()
                .map(Product::toDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getFilteredProducts(String category, String producer, BigDecimal minPrice, BigDecimal maxPrice) {

        if (Objects.nonNull(minPrice) && Objects.nonNull(maxPrice) && minPrice.compareTo(maxPrice) > 0) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("Price", "Min price is higher than max price")));
        }

        return productRepository.findAll()
                .stream()
                .filter(product -> !Objects.nonNull(producer) || Objects.equals(product.getProducer().getName(), producer))
                .filter(product -> !Objects.nonNull(category) || Objects.equals(product.getCategory().getName(), category))
                .filter(product -> !Objects.nonNull(minPrice) || product.getPrice().compareTo(minPrice) >= 0)
                .filter(product -> !Objects.nonNull(maxPrice) || product.getPrice().compareTo(maxPrice) <= 0)
                .map(Product::toDto)
                .collect(Collectors.toList());

    }
}
