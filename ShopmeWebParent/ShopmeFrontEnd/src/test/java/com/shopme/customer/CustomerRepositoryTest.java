package com.shopme.customer;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.enums.AuthenticationType;
import com.shopme.setting.CountryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testCreateCustomer() {
        Country vietNam = countryRepository.findById(242).get();
        Customer customer = new Customer();

        customer.setEmail("benzema9@gmail.com");
        customer.setPassword("benzema9depzai");
        customer.setFirstName("Karim");
        customer.setLastName("Benzema");
        customer.setPhoneNumber("0912312312");
        customer.setAddressLine1("123 My Dinh");
        customer.setAddressLine2("321 Cau Giay");
        customer.setCity("Ha Noi");
        customer.setState("Ha Noi");
        customer.setPostalCode("z123");
        customer.setCreatedTime(new Date());
        customer.setCountry(vietNam);

        customerRepository.save(customer);
    }

    @Test
    public void testFindByVerificationCode() {
        Customer customer = customerRepository.findByVerificationCode("vC277zPNzHmu1I4sspnyz7ySABR4Zko2inrS9JjW9f1kKOmIgISWJEiXxJxZUf6K");
        System.err.println(customer.getEmail());
    }

    @Test
    public void testEnableCustomer() {
        Customer customer = customerRepository.findById(4).get();
        System.err.println(customer.isEnabled());
        System.err.println(customer.getVerificationCode());
    }

    @Test
    public void testUpdateAuthenticationType() {
        customerRepository.updateAuthenticationType(AuthenticationType.FACEBOOK, 1);
    }
}
