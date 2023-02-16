package com.shopme.admin.customer;

import com.shopme.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("""
            SELECT c FROM Customer c
            WHERE CONCAT(c.firstName, ' ', c.lastName, ' ', c.email, ' ', c.addressLine1, ' ', c.addressLine2, ' ', c.city, ' ', c.state, ' ', c.country.name, ' ', c.postalCode)
            LIKE %:keyword%
            """)
    Page<Customer> findCustomerByPage(@Param("keyword") String keyword, Pageable pageable);

    Customer findByEmail(String email);
}
