package com.shopme.admin.brand;

import com.shopme.common.dto.CategoryDto;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@Slf4j
public class BrandRestController {

    private final BrandService brandService;

    @GetMapping("/brands/{id}/get-categories")
    public List<CategoryDto> getCategoriesByBrand(@PathVariable("id") int id) {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        try {
            Brand brand = brandService.getBrandById(id);
            Set<Category> categoryDtoSet = brand.getCategories();
            for(Category category : categoryDtoSet) {
                CategoryDto categoryDto = new CategoryDto(category.getId(), category.getName());
                categoryDtoList.add(categoryDto);
            }
        } catch(BrandNotFoundException ex) {
            log.error(ex.getMessage());
        }
        return categoryDtoList;
    }

}
