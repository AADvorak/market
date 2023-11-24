package net.thumbtack.school.market.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceDtoRequest {

    @Min(1)
    private int productId;

    @Min(1)
    private int shopId;

    @Positive
    private BigDecimal price;
}
