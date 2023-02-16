package com.shopme.product;

import com.shopme.common.entity.Product;
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
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testFindProductByCategory() {
//        List<Product> productList = productRepository.findProductByCategory("-1-");
//        System.err.println(productList.size());
    }

    @Test
    public void testGetProductByAlias() {
        Product product = productRepository.findByAlias("canon-eos-m50");
        if(product != null) {
            System.err.println(product.getName());
        }
    }

    @Test
    public void testFindByFullTextSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = productRepository.findByFullTextSearch("", pageable);
        System.err.println(productPage.getContent().size());
    }
}
