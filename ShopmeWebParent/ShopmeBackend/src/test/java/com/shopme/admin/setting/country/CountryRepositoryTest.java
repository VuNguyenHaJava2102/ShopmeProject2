package com.shopme.admin.setting.country;

import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testGetAllCountries() {
        List<Country> countryList = countryRepository.findAllByOrderByName();
        countryList.forEach(c -> System.out.println(c));
    }

    @Test
    public void testCreateCountry() {
        Country vietNam = new Country("Viet Nam", "VIE");
        Country usa = new Country("United States of America", "USA");
        Country china = new Country("China", "CHI");
        Country france = new Country("France", "FRA");
        Country japan = new Country("Japan", "JAP");
        Country korea = new Country("South Korea", "KOR");

        countryRepository.saveAll(List.of(vietNam, usa, china, france, japan, korea));
    }
}
