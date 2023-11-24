package net.thumbtack.school.market.repository;

import net.thumbtack.school.market.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>, JpaRepository<Product, Integer> {

    Page<Product> findByVendorCodeLikeIgnoreCaseOrNameLikeIgnoreCase(String vendorCode, String name, Pageable pageable);

}
