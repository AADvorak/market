package net.thumbtack.school.market.repository;

import net.thumbtack.school.market.model.Price;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PriceRepository extends PagingAndSortingRepository<Price, Integer>, JpaRepository<Price, Integer> {

    Page<Price> findByProductId(int productId, Pageable pageable);

}
