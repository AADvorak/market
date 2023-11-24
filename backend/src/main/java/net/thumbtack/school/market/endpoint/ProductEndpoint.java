package net.thumbtack.school.market.endpoint;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.market.dto.request.EditProductDtoRequest;
import net.thumbtack.school.market.dto.request.ProductDtoRequest;
import net.thumbtack.school.market.dto.response.ProductDtoResponse;
import net.thumbtack.school.market.dto.response.DataWithCountsDtoResponse;
import net.thumbtack.school.market.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class ProductEndpoint {

    private final ProductService productService;

    @QueryMapping
    public DataWithCountsDtoResponse<ProductDtoResponse> products(
            @Argument String filter,
            @Argument int page,
            @Argument int size
    ) {
        return productService.getAll(filter, page, size);
    }

    @QueryMapping
    public ProductDtoResponse product(@Argument int id) {
        return productService.getById(id);
    }

    @MutationMapping
    public ProductDtoResponse addProduct(@Argument("input") @Valid ProductDtoRequest product) {
        return productService.add(product);
    }

    @MutationMapping
    public void editProduct(@Argument("input") @Valid EditProductDtoRequest request) {
        productService.edit(request);
    }

    @MutationMapping
    public void deleteProduct(@Argument int id) {
        productService.delete(id);
    }

}
