package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateFirstRole() {
        Role admin = new Role("Admin", "Manage everything");
        Role savedRole = roleRepository.save(admin);

        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void createRestRoles() {
        Role salesperson = new Role("Salesperson", "Manage product price, customers, shipping, orders and sales report");
        Role editor = new Role("Editor", "Manage categories, brands, products, articles, menus");
        Role shipper = new Role("Shipper", "View products, view orders and update order status");
        Role assistant = new Role("Assistant", "Manage questions and reviews");

        roleRepository.saveAll(List.of(salesperson, editor, shipper, assistant));
    }
}
