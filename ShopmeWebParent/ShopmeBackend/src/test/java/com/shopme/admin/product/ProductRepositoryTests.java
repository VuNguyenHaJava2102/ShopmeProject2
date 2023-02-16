package com.shopme.admin.product;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct() {
        Brand brand = entityManager.find(Brand.class, 37);
        Category category = entityManager.find(Category.class, 6);

        Product product = new Product();

        product.setName("Acer Aspire 7");
        product.setAlias("acer_aspire_7");
        product.setShortDescription("A short description for Acer Aspire 7");
        product.setFullDescription("A full description for Acer Aspire 7");
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(789);
        product.setCost(600);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        productRepository.save(product);
    }

    @Test
    public void testSaveProductWithExtraImage() {
        Product product = entityManager.find(Product.class, 5);
        product.setMainImage("main-image.png");

        product.addExtraImage("extra-image1.jpg");
        product.addExtraImage("extra-image2.jpg");
        product.addExtraImage("extra-image3.jpg");

        productRepository.save(product);
    }

    @Test
    public void testSaveProductWithDetails() {
        Product product = entityManager.find(Product.class, 9);

        product.addDetail("Device Memory", "128GB");
        product.addDetail("CPU Model", "Mediatek");
        product.addDetail("OS", "Android 10");

        productRepository.save(product);
    }

}
