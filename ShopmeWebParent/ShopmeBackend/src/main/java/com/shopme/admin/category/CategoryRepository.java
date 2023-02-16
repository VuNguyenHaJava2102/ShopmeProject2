package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // This method is used for get categories in form
    @Query("SELECT c FROM Category c WHERE c.parent.id IS NULL")
    List<Category> findRootCategories(Sort sort);

    // This method is used for get categories in table
    @Query("SELECT c FROM Category c WHERE c.parent.id IS NULL")
    Page<Category> findRootCategories(Pageable pageable);

    // This method is used for search category by name
    @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
    Page<Category> searchCategories(String keyword, Pageable pageable);

    Category findByName(String name);

    Category findByAlias(String alias);

    Boolean existsByName(String name);

    Boolean existsByAlias(String alias);

}
