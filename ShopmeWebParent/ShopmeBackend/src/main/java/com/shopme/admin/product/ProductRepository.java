package com.shopme.admin.product;

import com.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByName(String name);

    Product findByAlias(String alias);

    Boolean existsByName(String name);

    Boolean existsByAlias(String alias);

    @Query("SELECT p FROM Product p WHERE CONCAT(p.name, ' ', p.alias, ' ', p.shortDescription, ' '," +
            "p.fullDescription, ' ', p.brand.name, ' ', p.category.name) LIKE %?1% AND p.category.allParentIds LIKE %?2%")
    Page<Product> searchProduct(String keyword, String allParentIds, Pageable pageable);

}
