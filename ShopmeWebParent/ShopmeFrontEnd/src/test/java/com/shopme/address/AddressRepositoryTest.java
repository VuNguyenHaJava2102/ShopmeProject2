package com.shopme.address;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateNewAddress() {
        Customer customer = entityManager.find(Customer.class, 1);
        Country country = entityManager.find(Country.class, 8);

        Address address = new Address();

        address.setFirstName("Toni");
        address.setLastName("Kroos");
        address.setPhoneNumber("0947202198");
        address.setAddressLine1("Croatia 1");
        address.setAddressLine2("Croatia 2");
        address.setCity("Hai Phong");
        address.setState("Bac Trung Bo");
        address.setPostalCode("zo0o92");
        address.setDefaultForShipping(true);
        address.setCustomer(customer);
        address.setCountry(country);

        addressRepository.save(address);
    }

    @Test
    public void testFindByIdAndCustomerId() {
        Address address = addressRepository.findByIdAndCustomerId(1, 1);
        System.err.println(address);
    }

    @Test
    public void testDeleteByIdAndCustomerId() {
        addressRepository.deleteByIdAndCustomerId(1, 1);
    }

    @Test
    public void testFindAllByCustomer() {
        Customer customer = entityManager.find(Customer.class, 1);
        List<Address> addressList = addressRepository.findByCustomer(customer);
        System.err.println(addressList.size());
    }

    @Test
    public void testfindDefaultAddressOfCustomer() {
        Address address = addressRepository.findDefaultAddressOfCustomer(5);
        System.err.println(address.getAddressLine1());
    }

    @Test
    public void test() {
        Address address = entityManager.find(Address.class, 3);
        System.err.println(address.getState().isBlank());
    }
}
