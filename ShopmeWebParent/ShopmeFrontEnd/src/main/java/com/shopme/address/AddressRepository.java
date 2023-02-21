package com.shopme.address;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByCustomer(Customer customer);

    Address findByIdAndCustomerId(int addressId, int customerId);

    void deleteByIdAndCustomerId(int addressId, int customerId);

    @Query("""
            UPDATE Address a
            SET a.defaultForShipping = true
            WHERE a.id = :id
            """)
    @Modifying
    void setDefaultForAnAddress(@Param("id") int id);

    @Query("""
            UPDATE Address a
            SET a.defaultForShipping = false
            WHERE a.id <> :addressId AND a.customer.id = :customerId
            """)
    @Modifying
    void setNonDefaultForRestAddresses(@Param("addressId") int addressId,
                                       @Param("customerId") int customerId);

    @Query("""
            SELECT a FROM Address a
            WHERE a.customer.id = :customerId AND a.defaultForShipping = true
            """)
    Address findDefaultAddressOfCustomer(@Param("customerId") int customerId);
}
