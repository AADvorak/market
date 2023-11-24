package net.thumbtack.school.market.service;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.market.dto.request.EditProductDtoRequest;
import net.thumbtack.school.market.dto.request.ProductDtoRequest;
import net.thumbtack.school.market.dto.response.ProductDtoResponse;
import net.thumbtack.school.market.dto.response.DataWithCountsDtoResponse;
import net.thumbtack.school.market.exception.EntityNotFoundException;
import net.thumbtack.school.market.exception.ValueAlreadyExistsGQLException;
import net.thumbtack.school.market.model.Product;
import net.thumbtack.school.market.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper mapper = new ModelMapper();

    @PreAuthorize("hasAuthority('USER')")
    public DataWithCountsDtoResponse<ProductDtoResponse> getAll(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = filter != null && !filter.isEmpty()
                ? getFilteredProductsPage(filter, pageable)
                : productRepository.findAll(pageable);
        return new DataWithCountsDtoResponse<>(products.stream().map(this::mapProductToDto).toList(),
                products.getTotalElements(), products.getTotalPages());
    }

    @PreAuthorize("hasAuthority('USER')")
    public ProductDtoResponse getById(int id) {
        return mapProductToDto(productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("id", id)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public ProductDtoResponse add(ProductDtoRequest request) {
        Product product;
        try {
            product = productRepository.save(mapDtoToProduct(request));
        } catch (DataIntegrityViolationException ex) {
            throw new ValueAlreadyExistsGQLException("vendorCode", request.getVendorCode());
        }
        return mapProductToDto(product);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void edit(EditProductDtoRequest dto) {
        Product product = productRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException("id", dto.getId()));
        product.setVendorCode(dto.getVendorCode());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        try {
            productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            throw new ValueAlreadyExistsGQLException("vendorCode", product.getVendorCode());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(int id) {
        productRepository.deleteById(id);
    }

    private Page<Product> getFilteredProductsPage(String filter, Pageable pageable) {
        String filterAdapted = "%" + filter + "%";
        return productRepository.findByVendorCodeLikeIgnoreCaseOrNameLikeIgnoreCase(filterAdapted, filterAdapted, pageable);
    }

    private ProductDtoResponse mapProductToDto(Product product) {
        return mapper.map(product, ProductDtoResponse.class);
    }

    private Product mapDtoToProduct(ProductDtoRequest dto) {
        return mapper.map(dto, Product.class);
    }

}
