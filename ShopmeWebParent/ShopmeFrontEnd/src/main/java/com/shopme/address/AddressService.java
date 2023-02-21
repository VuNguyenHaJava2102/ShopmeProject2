package com.shopme.address;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.AddressNotFoundException;
import com.shopme.setting.CountryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final CountryRepository countryRepository;

    public List<Country> getAllCountriesOrderByName() {
        return countryRepository.findAllByOrderByName();
    }

    // Main service-function
    // 1
    public List<Address> getAllAddresses(Customer customer) {
        return addressRepository.findByCustomer(customer);
    }

    // 2
    public void saveAddress(Address address) {
        addressRepository.save(address);
    }

    // 3
    public Address getAddressById(int id) throws AddressNotFoundException {
        Optional<Address> addressOptional = addressRepository.findById(id);
        if(addressOptional.isEmpty()) {
            throw new AddressNotFoundException("Could not find any address!");
        }
        return addressOptional.get();
    }

    // 4
    public void deleteAddressById(int id) throws AddressNotFoundException {
        Address address = getAddressById(id);
        addressRepository.delete(address);
    }

    // 5
    @Transactional
    public void setDefaultAddressForAnAddress(int addressId,
                                              int customerId) {
        if(addressId > 0) {
            addressRepository.setDefaultForAnAddress(addressId);
        }
        addressRepository.setNonDefaultForRestAddresses(addressId, customerId);
    }

    // 6
    public Address getDefaultAddressOfCustomer(int customerId) {
        return addressRepository.findDefaultAddressOfCustomer(customerId);
    }
}
