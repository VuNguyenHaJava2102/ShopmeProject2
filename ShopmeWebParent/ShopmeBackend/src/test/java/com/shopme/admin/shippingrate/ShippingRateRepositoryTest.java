package com.shopme.admin.shippingrate;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ShippingRateRepositoryTest {

    @Autowired
    private ShippingRateRepository shippingRateRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateShippingRate() {
        Country country = entityManager.find(Country.class, 96);

        ShippingRate shippingRate = new ShippingRate();
        shippingRate.setRate(1.7f);
        shippingRate.setDays(1);
        shippingRate.setCodSupported(true);
        shippingRate.setCountry(country);
        shippingRate.setState("state 1 of country 96");

        shippingRateRepository.save(shippingRate);
    }

    @Test
    public void testFindByCountryAndState() {
        Country country = entityManager.find(Country.class, 96);

        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(country, "state 1 of country 96");
        System.err.println(shippingRate.toString());
    }
}
