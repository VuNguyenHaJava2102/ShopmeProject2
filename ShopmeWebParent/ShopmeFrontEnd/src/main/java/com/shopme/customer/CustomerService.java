package com.shopme.customer;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.enums.AuthenticationType;
import com.shopme.setting.CountryRepository;
import lombok.AllArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. Get all countries order by name
    public List<Country> getAllCountries() {
        return countryRepository.findAllByOrderByName();
    }

    // 2
    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer == null;
    }

    // 3
    public Customer createCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());

        String verificationCode = RandomString.make(64);
        customer.setVerificationCode(verificationCode);
        return customerRepository.save(customer);
    }

    // 3.1
    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    // 4
    public boolean verifyAccount(String verificationCode) {
        Customer customer = customerRepository.findByVerificationCode(verificationCode);
        if(customer == null) { // Invalid code
            return false;
        } else {
            if(customer.isEnabled()) { // Customer has been enabled
                return false;
            } else {
                customerRepository.enableCustomer(customer.getId());
                return true;
            }
        }
    }

    // 5
    public void updateAuthenticationType(Customer customer, AuthenticationType type) {
        if(!customer.getAuthenticationType().equals(type)) {
            customerRepository.updateAuthenticationType(type, customer.getId());
        }
    }
}
