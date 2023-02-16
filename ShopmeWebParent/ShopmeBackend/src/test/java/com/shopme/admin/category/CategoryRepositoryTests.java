package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testCreateRootCategory() {
        Category category = new Category("Electronics");
        categoryRepository.save(category);
    }

    @Test
    public void testCreateSubCategory() {
        Category parent = categoryRepository.findById(7).get();

        Category subCategory1 = new Category("Memory Drive", parent);

//        categoryRepository.saveAll(List.of(subCategory1, subCategory2));
        categoryRepository.save(subCategory1);
    }

    @Test
    public void testGetCategory() {
        Category parent = categoryRepository.findById(4).get();
        System.err.println(parent.getName());
    }

    @Test
    public void testGetCategoriesInHierarchical() {
        List<Category> allCategories = categoryRepository.findAll();
        for(Category category : allCategories) {
            if(category.getParent() == null) {
                System.out.println(category.getName());
                Set<Category> children = category.getChildren();

                for(Category child : children) {
                    System.out.println("--" + child.getName());
                    getSubCategory(child, 1);
                }
            }
        }
    }

    private void getSubCategory(Category category, int level) {
        if(category.getChildren().size() == 0) return;

        ++level;
        for(Category subCategory : category.getChildren()) {
            for(int i = 1; i <= level; ++i) {
                System.out.print("--");
            }
            System.out.println(subCategory.getName());
            getSubCategory(subCategory, level);
        }
    }

    @Test
    public void testCheckNameUnique() {
        System.err.println(categoryRepository.existsByAlias("computers"));
    }

    @Test
    public void testSearchCategories() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by("name"));
        Page<Category> categoryPage = categoryRepository.searchCategories("com", pageable);
        System.err.println(categoryPage.getContent().size());
    }

    @Test
    public void insertAllParentIds() {
        List<Category> categoryList = categoryRepository.findAll();
        for(Category category : categoryList) {
            category.setAllParentIds("");
            String allParentIds = getAllParentIds(category);
            category.setAllParentIds(allParentIds);
            categoryRepository.save(category);
        }
    }

    private String getAllParentIds(Category category) {
        String text = "-" + category.getId() + "-";

        Category parent = category.getParent();

        if(parent != null) {
            text += parent.getId() + "-";
            text = continueConcatenate(parent, text);
        }
        return text;
    }

    private String continueConcatenate(Category category,
                                       String text) {
        Category parent = category.getParent();

        if(parent != null) {
            text += parent.getId() + "-";
            text = continueConcatenate(parent, text);
        }
        return text;
    }

}
