package net.thumbtack.school.market.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditPriceDtoRequest {

    @Positive
    private int id;

    @Positive
    private BigDecimal price;

}
