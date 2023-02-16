package com.shopme.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testGetAllEnabledCategories() {
        List<Category> categoryList = categoryRepository.findAllEnabled();
        categoryList.forEach(c -> System.out.println(c.getName()));
    }

    @Test
    public void testCategoryChildrenSize() {
        Category category = categoryRepository.findById(1).get();
        System.err.println(category.getChildren().size() == 0);
    }

    @Test
    public void testFindEnabledCategoryByAlias() {
        Category category = categoryRepository.findEnabledCategoryByAlias("computer_network_cards");
        System.err.println(category.getName());
    }
}
