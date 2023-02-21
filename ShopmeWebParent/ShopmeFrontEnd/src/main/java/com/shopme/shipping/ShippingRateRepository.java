package com.shopme.shipping;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {

    ShippingRate findByCountryAndState(Country country, String state);

}
