package com.shopme.admin.shippingrate;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {

    @Query("""
            SELECT sr FROM ShippingRate sr
            WHERE CONCAT(sr.country.name, ' ', sr.state) LIKE %:keyword%
            """)
    Page<ShippingRate> findAllByPageAndKeyword(@Param("keyword") String keyword, Pageable pageable);

    ShippingRate findByCountryAndState(Country country, String state);
}
