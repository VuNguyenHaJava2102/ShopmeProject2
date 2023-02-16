package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@AllArgsConstructor
public class CategoryService {

    public static final int NUMBER_OF_ROOT_CATEGORIES_PER_PAGE = 1;

    private final CategoryRepository categoryRepository;

    // Get all categories in hierarchical form use in table
    public List<Category> getAllCategories(int pageNum,
                                           String sortDir,
                                           String keyword,
                                           CategoryPageInfo pageInfo) {
        Sort sort = Sort.by("name");
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_OF_ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> categoryPage = null;
        if(!keyword.equals("")) {
            categoryPage = categoryRepository.searchCategories(keyword, pageable);
        } else {
            categoryPage = categoryRepository.findRootCategories(pageable);
        }

        pageInfo.setTotalElements(categoryPage.getTotalElements());
        pageInfo.setTotalPages(categoryPage.getTotalPages());

        if(!keyword.equals("")) {
            // We copy all categories to add isHasChildren field
            List<Category> categoriesFromPageResult = categoryPage.getContent();
            List<Category> copyCategories = new ArrayList<>();
            for(Category category : categoriesFromPageResult) {
                copyCategories.add(Category.copyAll(category));
            }
            return copyCategories;
        } else {
            List<Category> rootCategoryList = categoryPage.getContent();
            return getCategoriesInHierarchicalForm(rootCategoryList, sortDir);
        }
    }

    public List<Category> getCategoriesInHierarchicalForm(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for(Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyAll(rootCategory));
            if(rootCategory.getChildren().size() == 0) continue;

            Set<Category> sortedChildren = sortSubCategories(rootCategory.getChildren(), sortDir);
            for(Category subCategory : sortedChildren) {
                String newName = "--" + subCategory.getName();
                subCategory.setName(newName);
                hierarchicalCategories.add(Category.copyAll(subCategory));

                addSubCategories1(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }
        return hierarchicalCategories;
    }

    private void addSubCategories1(List<Category> categories,
                                   Category parent,
                                   int level,
                                   String sortDir) {
        if(parent.getChildren().size() == 0) return;
        ++level;

        Set<Category> sortedChildren = sortSubCategories(parent.getChildren(), sortDir);
        for(Category childCategory : sortedChildren) {
            String newName = "";
            for(int i = 0; i < level; ++i) {
                newName += "--";
            }
            newName += childCategory.getName();
            childCategory.setName(newName);
            categories.add(Category.copyAll(childCategory));

            addSubCategories1(categories, childCategory, level, sortDir);
        }
    }

    // Get all categories in hierarchical form to use in form
    public List<Category> getCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();

        for(Category rootCategory : categoryRepository.findRootCategories(Sort.by("name").ascending())) {
            categoriesUsedInForm.add(Category.copyIdAndName(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), "asc");
            for(Category subCategory : children) {
                String name = "--" + subCategory.getName();
                subCategory.setName(name);
                categoriesUsedInForm.add(Category.copyIdAndName(subCategory));

                addSubCategories2(categoriesUsedInForm, subCategory, 1);
            } 
        }
        return categoriesUsedInForm;
    }

    private void addSubCategories2(List<Category> categoriesUsedInForm,
                                  Category category,
                                  int level) {
        if(category.getChildren().size() == 0) return;
        ++level;

        Set<Category> children = sortSubCategories(category.getChildren(), "asc");
        for(Category subCategory : children) {
            String name = "";
            for(int i = 1; i <= level; ++i) {
                name += "--";
            }
            name += subCategory.getName();
            subCategory.setName(name);
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory));
            addSubCategories2(categoriesUsedInForm, subCategory, level);
        }
    }

    public SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                if(sortDir.equals("asc")) {
                    return c1.getName().compareTo(c2.getName());
                } else {
                    return c2.getName().compareTo(c1.getName());
                }
            }
        });
        sortedChildren.addAll(children);
        return sortedChildren;
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

//    public int getTotalRootCategories() {
//        return categoryRepository.findRootCategories(Sort.by("name")).size();
//    }
//
//    public int getTotalPages() {
//        int totalPages = getTotalRootCategories() / NUMBER_OF_ROOT_CATEGORIES_PER_PAGE;
//        int remainder = getTotalRootCategories() % NUMBER_OF_ROOT_CATEGORIES_PER_PAGE;
//
//        if(remainder != 0) {
//            return totalPages + 1;
//        }
//        return totalPages;
//    }

    public Category getCategoryById(int id) throws CategoryNotFoundException {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()) {
            throw new CategoryNotFoundException("Could not find category with ID " + id);
        }
        return category.get();
    }

    // Check uniqueness of name and alias
    public boolean checkNameDuplicated(Integer id, String name) {
        if(id == null) return categoryRepository.existsByName(name);

        Category categoryByName = categoryRepository.findByName(name);
        if(categoryByName == null) return false;

        if(categoryByName.getId() == id) return false;
        return true;
    }

    public boolean checkAliasDuplicated(Integer id, String alias) {
        if(id == null) return categoryRepository.existsByAlias(alias);

        Category categoryByAlias = categoryRepository.findByAlias(alias);
        if(categoryByAlias == null) return false;

        if(categoryByAlias.getId() == id) return false;
        return true;
    }

    public void updateCategoryStatus(int id) throws CategoryNotFoundException {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()) {
            throw new CategoryNotFoundException("Could not find category with ID " + id);
        }
        category.get().setEnabled(!category.get().isEnabled());
        categoryRepository.save(category.get());
    }

    public void deleteCategory(int id) throws CategoryNotFoundException {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()) {
            throw new CategoryNotFoundException("Could not find category with ID " + id);
        }
        categoryRepository.delete(category.get());
    }

}
