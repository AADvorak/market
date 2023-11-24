package net.thumbtack.school.market.mapper;

import net.thumbtack.school.market.dto.response.PriceDtoResponse;
import net.thumbtack.school.market.model.Price;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class PriceMapper {

    private final ModelMapper mapper = new ModelMapper();

    public PriceMapper() {
        TypeMap<Price, PriceDtoResponse> modelToDto
                = mapper.createTypeMap(Price.class, PriceDtoResponse.class);
        modelToDto.addMappings(
                mapper -> mapper.map(src -> src.getShop().getId(), PriceDtoResponse::setShopId)
        );
        modelToDto.addMappings(
                mapper -> mapper.map(src -> src.getShop().getName(), PriceDtoResponse::setShopName)
        );
    }

    public PriceDtoResponse modelToDto(Price price) {
        return mapper.map(price, PriceDtoResponse.class);
    }

}
