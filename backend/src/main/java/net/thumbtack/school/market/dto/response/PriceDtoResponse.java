package net.thumbtack.school.market.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceDtoResponse {

    private int id;

    private int shopId;

    private String shopName;

    private BigDecimal price;

}
