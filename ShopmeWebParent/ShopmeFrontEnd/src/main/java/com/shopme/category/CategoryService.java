package com.shopme.category;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 1
    public List<Category> getAllCategoryHasNoChildren() {
        List<Category> expectedList = new ArrayList<>();
        List<Category> enabledCategoryList = categoryRepository.findAllEnabled();

        enabledCategoryList.forEach(c -> {
            if(c.getChildren().size() == 0)
                expectedList.add(c);
        });
        return expectedList;
    }

    public Category getEnabledCategoryByAlias(String alias) throws CategoryNotFoundException {
        Category category = categoryRepository.findEnabledCategoryByAlias(alias);
        if(category == null) {
            throw new CategoryNotFoundException("Could not find any category with alias: " + alias);
        }
        return categoryRepository.findEnabledCategoryByAlias(alias);
    }

    // 2
    public List<Category> getAllParentsOfCategory(Category category) {
        List<Category> parentCategoryList = new ArrayList<>();

        while(true) {
            Category parent = category.getParent();
            if(parent != null) {
                parentCategoryList.add(0, parent);
                category = parent;
            } else {
                break;
            }
        }
        return parentCategoryList;
    }

}
