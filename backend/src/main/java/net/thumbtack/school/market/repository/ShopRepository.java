package net.thumbtack.school.market.repository;

import net.thumbtack.school.market.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ShopRepository extends PagingAndSortingRepository<Shop, Integer>, JpaRepository<Shop, Integer>,
        JpaSpecificationExecutor<Shop> {}
