package com.shopme.product;

import com.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("""
            SELECT p FROM Product p
            WHERE p.category.allParentIds LIKE %:allParentIds%
            AND p.enabled = true
            ORDER BY p.name
            """)
    Page<Product> findProductByCategory(@Param("allParentIds") String allParentIds,
                                        Pageable pageable);

    Product findByAlias(String alias);

    @Query(value = """
            SELECT * FROM products
            where enabled = TRUE AND MATCH(name, short_description, full_description) AGAINST (:keyword)
            """, nativeQuery = true)
    Page<Product> findByFullTextSearch(@Param("keyword") String keyword, Pageable pageable);
}
