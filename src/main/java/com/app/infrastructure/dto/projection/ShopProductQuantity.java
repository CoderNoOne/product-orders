package com.app.infrastructure.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShopProductQuantity {

    private String shopName;
    private Integer productQuantity;
}
