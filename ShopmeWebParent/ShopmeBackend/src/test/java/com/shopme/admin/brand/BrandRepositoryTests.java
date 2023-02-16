package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BrandRepositoryTests {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testCreateNewBrand() {
//        Category laptops = entityManager.find(Category.class, 6);
//
//        Set<Category> categories = new HashSet<>();
//        categories.add(laptops);
//
//        Brand acer = new Brand("Acer", "brand-logo.png", categories);
//        brandRepository.save(acer);

//        Category cellPhones = entityManager.find(Category.class, 16);
//        Category tablets = entityManager.find(Category.class, 7);
//
//        Set<Category> categories = new HashSet<>();
//        categories.add(cellPhones);
//        categories.add(tablets);
//
//        Brand apple = new Brand("Apple", "brand-logo.png", categories);
//        brandRepository.save(apple);

        Category memory = entityManager.find(Category.class, 29);
        Category hardDrive = entityManager.find(Category.class, 24);

        Set<Category> categories = new HashSet<>();
        categories.add(memory);
        categories.add(hardDrive);

        Brand samsung = new Brand("Samsung", "brand-logo.png", categories);
        brandRepository.save(samsung);
    }
}
