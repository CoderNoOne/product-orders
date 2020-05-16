package com.app.application.validators.impl;

import com.app.application.validators.generic.AbstractValidator;
import com.app.domain.repository.ShopRepository;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.dto.ManagerUpdateProductOrderProposalDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@SessionScope
@Component
@RequiredArgsConstructor
public class ManagerUpdateProductProposalDtoValidator extends AbstractValidator<ManagerUpdateProductOrderProposalDto> {

    private final ShopRepository shopRepository;
    private final StockRepository stockRepository;

    @Override
    public Map<String, String> validate(ManagerUpdateProductOrderProposalDto managerUpdateProductOrderProposalDto) {

        errors.clear();

        if (Objects.isNull(managerUpdateProductOrderProposalDto)) {
            errors.put("ManagerUpdateProductProposalDto", "is null");
        }

        if (Objects.isNull(managerUpdateProductOrderProposalDto.getDiscount())) {
            errors.put("Discount", "is null");
        } else if (!isDiscountValid(managerUpdateProductOrderProposalDto.getDiscount())) {
            errors.put("Discount value", "must be in the range <0,100>");
        }

        if (Objects.isNull(managerUpdateProductOrderProposalDto.getShopName())) {
            errors.put("Shop name", "is null");
        } else {

            if (!doShopExists(managerUpdateProductOrderProposalDto.getShopName())) {
                errors.put("Shop object", "No shop with name " + managerUpdateProductOrderProposalDto.getShopName());
            }

//            if (doShopExists(managerUpdateProductOrderProposalDto.getShopName()
//                    && !doProductExistInShop(managerUpdateProductOrderProposalDto.getShopName(), managerUpdateProductOrderProposalDto.getProductInfo()))
//            ) {
//                errors.put("")
//            }
        }

        return errors;
    }

//    private boolean doProductExistInShop(String shopName, ProductInfo productInfo) {
//
//        return shopRepository.;
//    }

    private boolean doShopExists(String shopName) {
        return shopRepository.findByName(shopName).isPresent();
    }

    private boolean isDiscountValid(BigDecimal discount) {
        return discount.compareTo(BigDecimal.ZERO) >= 0 &&
                discount.compareTo(new BigDecimal("100")) <= 0;
    }
}
