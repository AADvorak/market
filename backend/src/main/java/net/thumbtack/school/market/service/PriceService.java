package net.thumbtack.school.market.service;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.market.dto.request.EditPriceDtoRequest;
import net.thumbtack.school.market.dto.request.PriceDtoRequest;
import net.thumbtack.school.market.dto.response.PriceDtoResponse;
import net.thumbtack.school.market.dto.response.DataWithCountsDtoResponse;
import net.thumbtack.school.market.exception.EntityNotFoundException;
import net.thumbtack.school.market.exception.ValueAlreadyExistsGQLException;
import net.thumbtack.school.market.mapper.PriceMapper;
import net.thumbtack.school.market.model.Price;
import net.thumbtack.school.market.model.Product;
import net.thumbtack.school.market.model.Shop;
import net.thumbtack.school.market.repository.PriceRepository;
import net.thumbtack.school.market.repository.ProductRepository;
import net.thumbtack.school.market.repository.ShopRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final ProductRepository productRepository;

    private final ShopRepository shopRepository;

    private final PriceRepository priceRepository;

    private final PriceMapper mapper;

    @PreAuthorize("hasAuthority('USER')")
    public DataWithCountsDtoResponse<PriceDtoResponse> getAllByProduct(
            int productId, String sortDir, int page, int size
    ) {
        Sort sort = Sort.by("price");
        if (Objects.equals(sortDir, "desc")) {
            sort = sort.descending();
        }
        Page<Price> prices = priceRepository.findByProductId(productId,
                PageRequest.of(page, size, sort));
        return new DataWithCountsDtoResponse<>(prices.stream().map(mapper::modelToDto).toList(),
                prices.getTotalElements(), prices.getTotalPages());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public PriceDtoResponse add(PriceDtoRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("productId", request.getProductId()));
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new EntityNotFoundException("shopId", request.getShopId()));
        Price price = new Price(0, product, shop, request.getPrice());
        try {
            price = priceRepository.save(price);
        } catch (DataIntegrityViolationException ex) {
            throw new ValueAlreadyExistsGQLException("shopId", String.valueOf(request.getShopId()));
        }
        return mapper.modelToDto(price);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void edit(EditPriceDtoRequest request) {
        Price price = priceRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("id", request.getId()));
        price.setPrice(request.getPrice());
        priceRepository.save(price);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(int id) {
        priceRepository.deleteById(id);
    }

}
