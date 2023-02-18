package com.shopme.admin.shippingrate;

import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.exception.ShippingRateNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ShippingRateService {

    public static final int NUMBER_OF_SHIPPING_RATES_PER_PAGE = 10;

    private final ShippingRateRepository shippingRateRepository;
    private final CountryRepository countryRepository;

    // Sub service-function
    // 1
    public List<Country> getAllCountriesOrderByName() {
        return countryRepository.findAllByOrderByName();
    }

    // Main service-function
    // 1
    public Page<ShippingRate> getAllByPage(String sortField,
                                           String sortDir,
                                           String keyword,
                                           int pageNum) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_OF_SHIPPING_RATES_PER_PAGE, sort);

        if(keyword.equals("")) {
            return shippingRateRepository.findAll(pageable);
        }
        return shippingRateRepository.findAllByPageAndKeyword(keyword, pageable);
    }

    // 2
    public String checkCountryAndState(int id,
                                       int countryId,
                                       String state) {
        Country country = countryRepository.findById(countryId).get();
        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(country, state);

        if(shippingRate == null) {
            return "Ok";
        }
        if(shippingRate.getId() != id) {
            return "Duplicated";
        }
        return "OK";
    }

    // 3
    public ShippingRate saveShippingRate(ShippingRate shippingRate) {
        return shippingRateRepository.save(shippingRate);
    }

    // 4
    public ShippingRate getShippingRateById(int id) throws ShippingRateNotFoundException {
        Optional<ShippingRate> shippingRateOptional = shippingRateRepository.findById(id);
        if(shippingRateOptional.isEmpty()) {
            throw new ShippingRateNotFoundException("Could not find any shipping rate with ID " + id);
        }
        return shippingRateOptional.get();
    }

    // 5
    public void deleteShippingRate(int id) throws ShippingRateNotFoundException {
        ShippingRate shippingRate = getShippingRateById(id);
        shippingRateRepository.delete(shippingRate);
    }

    // 6
    public ShippingRate updateCodSupported(int id) throws ShippingRateNotFoundException {
        ShippingRate shippingRate = getShippingRateById(id);
        shippingRate.setCodSupported(!shippingRate.isCodSupported());
        return shippingRateRepository.save(shippingRate);
    }
}
