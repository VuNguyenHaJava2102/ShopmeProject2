package com.shopme.shipping;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ShippingRateService {

    private final ShippingRateRepository shippingRateRepository;

    public ShippingRate getShippingRateForCustomer(Customer customer) {
        String state = customer.getState();
        if(state.isBlank()) {
            state = customer.getCity();
        }
        return shippingRateRepository.findByCountryAndState(customer.getCountry(), state);
    }

    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();
        if(state.isBlank()) {
            state = address.getCity();
        }
        return shippingRateRepository.findByCountryAndState(address.getCountry(), state);
    }

}
