package com.shopme.admin.customer;

import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService {

    public static final int NUMBER_OF_CUSTOMERS_PER_PAGE = 10;

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    // 1
    public Page<Customer> getCustomersByPage(int pageNum,
                                             String sortField,
                                             String sortDir,
                                             String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_OF_CUSTOMERS_PER_PAGE, sort);
        Page<Customer> customerPage = null;

        if(keyword.equals("")) {
            customerPage = customerRepository.findAll(pageable);
            return customerPage;
        }
        customerPage = customerRepository.findCustomerByPage(keyword, pageable);
        return customerPage;
    }

    // 2
    public Customer updateCustomerStatus(int id) throws CustomerNotFoundException {
        Customer customer = getCustomerById(id);
        customer.setEnabled(!customer.isEnabled());
        return customerRepository.save(customer);
    }

    // 3
    public void deleteCustomer(int id) throws CustomerNotFoundException {
        getCustomerById(id);
        customerRepository.deleteById(id);
    }

    // 4
    public Customer getCustomerById(int id) throws CustomerNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if(optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException("Could not find any customer with ID: " + id);
        }
        return optionalCustomer.get();
    }

    // 5
    public boolean isEmailUnique(int id,
                                 String email) {
        Customer customer = customerRepository.findByEmail(email);
        if(customer == null) {
            return true;
        }
        if(customer.getId() != id) {
            return false;
        }
        return true;
    }

    // 6
    public Customer saveCustomer(Customer customerInForm) {
        Customer customerInDb = customerRepository.findById(customerInForm.getId()).get();
        if(!customerInForm.getPassword().equals(customerInDb.getPassword())) {
            encodePassword(customerInForm);
        }
        customerInForm.setCreatedTime(customerInDb.getCreatedTime());
        customerInForm.setEnabled(customerInDb.isEnabled());
        return customerRepository.save(customerInForm);
    }

    // 6.1
    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }
}
