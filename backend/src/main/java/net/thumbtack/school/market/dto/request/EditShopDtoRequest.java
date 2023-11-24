package net.thumbtack.school.market.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditShopDtoRequest extends ShopDtoRequest {

    @Min(1)
    private int id;

}
