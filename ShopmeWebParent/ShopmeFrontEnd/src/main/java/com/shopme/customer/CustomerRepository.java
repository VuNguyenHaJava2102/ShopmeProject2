package com.shopme.customer;

import com.shopme.common.entity.Customer;
import com.shopme.common.enums.AuthenticationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Customer findByEmail(String email);

    Customer findByVerificationCode(String code);

    @Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id = :id")
    @Modifying
    void enableCustomer(@Param("id") int id);

    @Query("UPDATE Customer c SET c.authenticationType = :type WHERE c.id = :id")
    @Modifying
    void updateAuthenticationType(@Param("type") AuthenticationType type,
                                  @Param("id") int id);
}
