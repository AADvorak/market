package net.thumbtack.school.market.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDtoResponse {

    private int id;

    private String vendorCode;

    private String name;

    private String description;
}
