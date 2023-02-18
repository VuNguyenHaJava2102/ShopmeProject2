package com.shopme.address;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final CountryRepository countryRepository;

    public List<Country> getAllCountriesOrderByName() {
        return countryRepository.findAllByOrderByName();
    }

    //
    public List<Address> getAllAddresses(Customer customer) {
        return addressRepository.findByCustomer(customer);
    }

    public void saveAddress(Address address) {
        if(address.getId() == null) {
            address.setDefaultForShipping(false);
        }
        addressRepository.save(address);
    }
}
