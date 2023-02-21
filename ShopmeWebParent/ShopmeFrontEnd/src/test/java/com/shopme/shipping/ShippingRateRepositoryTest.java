package com.shopme.shipping;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShippingRateRepositoryTest {

    @Autowired
    private ShippingRateRepository shippingRateRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void getByCountryAndState() {
        Country country = entityManager.find(Country.class, 234);
        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(country, "New York");

        System.err.println(shippingRate.toString());
    }
}
