package net.thumbtack.school.market.endpoint;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.market.dto.request.EditPriceDtoRequest;
import net.thumbtack.school.market.dto.request.PriceDtoRequest;
import net.thumbtack.school.market.dto.response.PriceDtoResponse;
import net.thumbtack.school.market.dto.response.DataWithCountsDtoResponse;
import net.thumbtack.school.market.service.PriceService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class PriceEndpoint {

    private final PriceService priceService;

    @QueryMapping
    public DataWithCountsDtoResponse<PriceDtoResponse> prices(
            @Argument int productId,
            @Argument String sortDir,
            @Argument int page,
            @Argument int size
    ) {
        return priceService.getAllByProduct(productId, sortDir, page, size);
    }

    @MutationMapping
    public PriceDtoResponse addPrice(@Valid @Argument("input") PriceDtoRequest request) {
        return priceService.add(request);
    }

    @MutationMapping
    public void editPrice(@Valid @Argument("input") EditPriceDtoRequest request) {
        priceService.edit(request);
    }

    @MutationMapping
    public void deletePrice(@Argument int id) {
        priceService.delete(id);
    }

}
