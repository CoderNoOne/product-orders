package com.app.application.service;

import com.app.application.validators.impl.AddStockToShopDtoValidator;
import com.app.application.validators.impl.CreateShopDtoValidator;
import com.app.application.validators.impl.UpdateShopDtoValidator;
import com.app.domain.entity.Address;
import com.app.domain.entity.Shop;
import com.app.domain.entity.Stock;
import com.app.domain.repository.ProductRepository;
import com.app.domain.repository.AddressRepository;
import com.app.domain.repository.ShopRepository;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.dto.AddStockToShopDto;
import com.app.infrastructure.dto.ShopDto;
import com.app.infrastructure.dto.UpdateShopDto;
import com.app.infrastructure.dto.createShop.CreateShopDto;
import com.app.infrastructure.dto.createShop.CreateStockDto;
import com.app.infrastructure.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final CreateShopDtoValidator createShopDtoValidator;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final UpdateShopDtoValidator updateShopDtoValidator;
    private final AddStockToShopDtoValidator addStockToShopDtoValidator;


    public Long addShop(CreateShopDto createShopDto) {

        var errors = createShopDtoValidator.validate(createShopDto);

        if (createShopDtoValidator.hasErrors()) {
            throw new ShopValidationException(Validations.createErrorMessage(errors));
        }

        var shop = createShopDto.toEntity();

        var address = shop.getAddress().getAddress();
        addressRepository.findByAddress(address)
                .ifPresentOrElse(shop::setAddress, () -> {
                    Address savedAddress = addressRepository.save(shop.getAddress());
                    shop.setAddress(savedAddress);
                });

        Set<Stock> stocks = shop.getStocks();

        stocks.forEach(stock ->
        {
            addressRepository.findByAddress(stock.getAddress().getAddress())
                    .ifPresentOrElse(stock::setAddress,
                            () -> {
                                Address savedAddress = addressRepository.save(stock.getAddress());
                                stock.setAddress(savedAddress);

                            });
        });

        Shop savedShop = shopRepository.save(shop);
        stocks.forEach(stock -> stock.setShop(savedShop));

        return savedShop.getId();
    }

    public void delete(Long id) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Shop id is null");
        }

        shopRepository.findOne(id)
                .ifPresentOrElse(
                        shopFromDb -> {
                            Set<Stock> stocks = shopFromDb.getStocks();
                            stockRepository.deleteAll(stocks);
                        },
                        () -> {
                            throw new NotFoundException("No shop with id: " + id);
                        }
                );

        shopRepository.deleteById(id);

    }


    public List<ShopDto> getAllShops() {

        return shopRepository.findAll()
                .stream()
                .map(Shop::toDto)
                .collect(Collectors.toList());
    }

    public ShopDto getShopById(Long id) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Id is null");
        }

        return shopRepository.findOne(id)
                .map(Shop::toDto)
                .orElseThrow(() -> new NotFoundException("No shop with id: " + id));
    }

    public Long updateShop(Long id, UpdateShopDto updateShopDto) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Id is null");
        }

        if (Objects.isNull(updateShopDto)) {
            throw new NullReferenceException("UpdateShopDto is null");
        }

        var errors = updateShopDtoValidator.validate(updateShopDto);

        if (updateShopDtoValidator.hasErrors()) {
            throw new ShopValidationException(Validations.createErrorMessage(errors));
        }

        shopRepository.findOne(id)
                .ifPresentOrElse(
                        shopFromDb -> {

                            if (updateShopDto.getAddress() != null) {
                                updateShopAddress(shopFromDb, updateShopDto.getAddress());
                            }

                            if (updateShopDto.getName() != null) {
                                shopFromDb.setName(updateShopDto.getName());
                            }
                            if (updateShopDto.getBudget() != null) {
                                shopFromDb.setBudget(updateShopDto.getBudget());
                            }
                        },
                        () -> {
                            throw new NotFoundException("No shop with id: " + id);
                        });
        return id;
    }

    private void updateShopAddress(Shop shop, String address) {

        addressRepository.findByAddress(address)
                .ifPresentOrElse(
                        shop::setAddress,
                        () -> {
                            Address savedAddress = addressRepository.save(Address.builder()
                                    .address(address)
                                    .build());
                            shop.setAddress(savedAddress);
                        }
                );
    }

    public Long updateStock(Long shopId, Long stockId, Map<String, String> params) {

        if (Objects.isNull(shopId)) {
            throw new NullIdValueException("Shop id is null");
        }

        if (Objects.isNull(stockId)) {
            throw new NullIdValueException("Stock id is null");
        }

        if (Objects.isNull(params)) {
            throw new NullReferenceException("Params map is null");
        }

        stockRepository.findByIdAndShopId(stockId, shopId)
                .ifPresentOrElse(
                        stockFromDb -> {
                            if (params.containsKey("address")) {

                                stockRepository.findByAddressAndShopId(params.get("address"), shopId).ifPresent((stock) -> {
                                    if (!Objects.equals(stock.getId(), stockId)) {
                                        throw new ConstraintViolationException("There is already a stock for shopId: " + shopId
                                                + " and address: " + params.get("address"));
                                    }
                                });

                                addressRepository.findByAddress(params.get("address"))
                                        .ifPresentOrElse(
                                                stockFromDb::setAddress,
                                                () -> {
                                                    Address savedAddress = addressRepository.save(Address.builder()
                                                            .address(params.get("address"))
                                                            .build());

                                                    stockFromDb.setAddress(savedAddress);
                                                }
                                        );
                            }
                        },
                        () -> {
                            throw new NotFoundException(MessageFormat.format("No stock for (stockId: {0}, shopId: {1})", stockId, shopId));
                        }
                );


        return stockId;
    }

    public List<ShopDto> getAllShopsWithProductInStore(Long id) {

        if (Objects.isNull(id)) {
            throw new NullIdValueException("Id is null");
        }

        if (!productRepository.existsById(id)) {
            throw new NotFoundException("No product with id: " + id);
        }

        return shopRepository.findAllShopsWithProductInStore(id)
                .stream()
                .map(Shop::toDto)
                .collect(Collectors.toList());
    }

    public Long createStockForShop(Long shopId, CreateStockDto createStockDto) {

        if (Objects.isNull(shopId)) {
            throw new NullIdValueException("Shop id is null");
        }

        if (Objects.isNull(createStockDto)) {
            throw new ValidationException("No body");
        }

        var addStockToShopDto = AddStockToShopDto.builder()
                .shopId(shopId)
                .address(createStockDto.getAddress())
                .build();

        var errors = addStockToShopDtoValidator.validate(addStockToShopDto);

        if (addStockToShopDtoValidator.hasErrors()) {
            throw new ShopValidationException(Validations.createErrorMessage(errors));
        }

        var stockIdWrapper = new AtomicLong();
        shopRepository.findOne(shopId)
                .ifPresentOrElse(
                        shopFromDb -> {
                            Stock stock = addStockToShopDto.toEntity();
                            stock.setShop(shopFromDb);
                            addressRepository.findByAddress(stock.getAddress().getAddress())
                                    .ifPresentOrElse(stock::setAddress,
                                            () -> {
                                                Address savedAddress = addressRepository.save(stock.getAddress());
                                                stock.setAddress(savedAddress);
                                            });

                            stockIdWrapper.set(stockRepository.save(stock).getId());
                        },
                        () -> {
                            throw new NotFoundException("No shop with id " + shopId);
                        }
                );

        return stockIdWrapper.get();
    }

    public void deleteStock(Long shopId, Long stockId) {

        if (Objects.isNull(shopId) || Objects.isNull(stockId)) {
            throw new NullIdValueException(
                    String.join(", ", Objects.isNull(shopId) ? "ShopId is null" : "", Objects.isNull(stockId) ? "StockId is null" : ""));
        }

        var errors = new HashMap<String, String>();
        stockRepository.findByIdAndShopId(stockId, shopId)
                .ifPresentOrElse(stockFromDb -> {
                            if (!(stockFromDb.getProductsQuantity().isEmpty() || stockFromDb.getProductsQuantity().values().stream().allMatch(i -> i == 0))) {
                                errors.put("Stock products", "There are products in store. Move them in another store before removing a stock");
                            } else {
                                stockRepository.delete(stockFromDb);
                            }
                        },
                        () -> errors.put("Stock object", "is not present"));

        if (!errors.isEmpty()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }
    }

    public Map<String, Integer> findProductQuantityGroupByShop(Long id) {

        return shopRepository.findAllShopsWithProductInStore(id)
                .stream()
                .collect(Collectors.groupingBy(
                        Shop::getName,
                        Collectors.flatMapping(
                                shop -> shop.getStocks()
                                        .stream()
                                        .flatMap(stock -> stock.getProductsQuantity().entrySet().stream())
                                        .filter(e -> e.getKey().getId().equals(id)),
                                Collectors.summingInt(Map.Entry::getValue)
                        )
                ));

    }
}
