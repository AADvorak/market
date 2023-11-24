package net.thumbtack.school.market.service;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.market.dto.request.EditShopDtoRequest;
import net.thumbtack.school.market.dto.request.ShopDtoRequest;
import net.thumbtack.school.market.dto.response.DataWithCountsDtoResponse;
import net.thumbtack.school.market.dto.response.ShopDtoResponse;
import net.thumbtack.school.market.exception.EntityNotFoundException;
import net.thumbtack.school.market.model.Price;
import net.thumbtack.school.market.model.Shop;
import net.thumbtack.school.market.repository.ShopRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    private final ModelMapper mapper = new ModelMapper();

    @PreAuthorize("hasAuthority('USER')")
    public DataWithCountsDtoResponse<ShopDtoResponse> getAll(String filter, int excludeProductId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Shop> specification = Specification
                .where(filter == null || filter.isEmpty() ? null : nameOrDescriptionLike(filter))
                .and(excludeProductId == 0 ? null : notExistsPriceFor(excludeProductId));
        Page<Shop> shops = shopRepository.findAll(specification, pageable);
        return new DataWithCountsDtoResponse<>(shops.stream().map(this::mapShopToDto).toList(),
                shops.getTotalElements(), shops.getTotalPages());
    }

    @PreAuthorize("hasAuthority('USER')")
    public ShopDtoResponse getById(int id) {
        return mapShopToDto(shopRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("id", id)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public ShopDtoResponse add(ShopDtoRequest shop) {
        return mapShopToDto(shopRepository.save(mapDtoToShop(shop)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void edit(EditShopDtoRequest dto) {
        Shop shop = shopRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException("id", dto.getId()));
        shop.setName(dto.getName());
        shop.setDescription(dto.getDescription());
        shopRepository.save(shop);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(int id) {
        shopRepository.deleteById(id);
    }

    private ShopDtoResponse mapShopToDto(Shop shop) {
        return mapper.map(shop, ShopDtoResponse.class);
    }

    private Shop mapDtoToShop(ShopDtoRequest dto) {
        return mapper.map(dto, Shop.class);
    }

    private static Specification<Shop> notExistsPriceFor(int excludeProductId) {
        return (root, query, builder) -> {
            Subquery<Price> subQuery = query.subquery(Price.class);
            Root<Price> priceRoot = subQuery.from(Price.class);
            return builder.not(builder.exists(subQuery.select(priceRoot)
                    .where(builder.equal(priceRoot.get("product").get("id"), excludeProductId),
                            builder.equal(priceRoot.get("shop").get("id"), root.get("id")))));
        };
    }

    private static Specification<Shop> nameOrDescriptionLike(String filter) {
        String filterAdapted = "%" + filter + "%";
        return (root, query, builder) -> builder.or(builder.like(root.get("name"), filterAdapted),
                builder.like(root.get("description"), filterAdapted));
    }

}
