package com.shopme.admin.setting.country;

import com.shopme.common.entity.Country;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class CountryRestController {

    private final CountryRepository countryRepository;

    @GetMapping("/countries/get-all")
    public List<Country> getAllCountriesOrderByName() {
        return countryRepository.findAllByOrderByName();
    }

    @PostMapping("/countries/save")
    public String saveCountry(@RequestBody Country country) {
        return countryRepository.save(country).getId().toString();
    }

    @DeleteMapping("/countries/delete/{id}")
    public void deleteCountry(@PathVariable("id") int id) {
        countryRepository.deleteById(id);
    }
}
