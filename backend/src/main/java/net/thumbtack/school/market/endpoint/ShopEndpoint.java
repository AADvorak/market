package net.thumbtack.school.market.endpoint;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.market.dto.request.EditShopDtoRequest;
import net.thumbtack.school.market.dto.request.ShopDtoRequest;
import net.thumbtack.school.market.dto.response.DataWithCountsDtoResponse;
import net.thumbtack.school.market.dto.response.ShopDtoResponse;
import net.thumbtack.school.market.service.ShopService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ShopEndpoint {

    private final ShopService shopService;

    @QueryMapping
    public DataWithCountsDtoResponse<ShopDtoResponse> shops(
            @Argument String filter,
            @Argument int excludeProductId,
            @Argument int page,
            @Argument int size
    ) {
        return shopService.getAll(filter, excludeProductId, page, size);
    }

    @QueryMapping
    public ShopDtoResponse shop(@Argument int id) {
        return shopService.getById(id);
    }

    @MutationMapping
    public ShopDtoResponse addShop(@Argument("input") @Valid ShopDtoRequest shop) {
        return shopService.add(shop);
    }

    @MutationMapping
    public void editShop(@Argument("input") @Valid EditShopDtoRequest shop) {
        shopService.edit(shop);
    }

    @MutationMapping
    public void deleteShop(@Argument int id) {
        shopService.delete(id);
    }

}
