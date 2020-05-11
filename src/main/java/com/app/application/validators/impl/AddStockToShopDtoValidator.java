package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.ShopRepository;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.dto.AddStockToShopDto;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@SessionScope
public class AddStockToShopDtoValidator extends AbstractValidator<AddStockToShopDto> {

    private final ShopRepository shopRepository;
    private final StockRepository stockRepository;

    @Override
    public Map<String, String> validate(AddStockToShopDto addStockToShopDto) {

        errors.clear();

        if (Objects.isNull(addStockToShopDto)) {
            super.errors.put("AddStockToShopDto object", "is null");
            return errors;
        }

        if (Objects.isNull(addStockToShopDto.getShopId())) {
            errors.put("Shop id", "is null");
        } else if (!isShopIdValid(addStockToShopDto.getShopId())) {
            errors.put("Shop id", "No shop with id: " + addStockToShopDto.getShopId());
        }

        if (Objects.isNull(addStockToShopDto.getAddress())) {
            errors.put("Stock address", "is null");
        } else if (!isStockAddressValid(addStockToShopDto.getAddress())) {
            errors.put("Stock address", "cannot be blank");
        } else if (isShopIdValid(addStockToShopDto.getShopId()) && !isAddressUnique(addStockToShopDto.getShopId(), addStockToShopDto.getAddress())) {
            errors.put("Stock address", "There is already a stock for this shop under this address");
        }


        return errors;
    }

    private boolean isAddressUnique(Long shopId, String address) {
        return stockRepository.findByAddressAndShopId(address, shopId).isEmpty();
    }

    private boolean isStockAddressValid(String address) {
        return Strings.isNotBlank(address);
    }

    private boolean isShopIdValid(Long shopId) {
        return shopRepository.findOne(shopId).isPresent();
    }
}
