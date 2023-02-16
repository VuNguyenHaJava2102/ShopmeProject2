package com.shopme.admin.customer;

import com.shopme.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testFindCustomerByPage() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Customer> customerPage = customerRepository.findCustomerByPage("@gmail", pageable);
        List<Customer> customerList = customerPage.getContent();
        System.err.println(customerList.size());
    }

    @Test
    public void testGetCustomerByEmail() {
        Customer customer = customerRepository.findByEmail("benzema9@gmail.com");
        System.err.println(customer.getFullName());
    }
}
