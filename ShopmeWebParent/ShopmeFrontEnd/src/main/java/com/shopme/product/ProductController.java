package com.shopme.product;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class ProductController {

    private final CategoryService categoryService;
    private final ProductService productService;

    // 1
    @GetMapping("/c/{category-alias}")
    public String viewProductsByCategoryFirstPage(@PathVariable("category-alias") String alias,
                                                  Model model) {
        return viewProductsByCategoryByPage(alias, 1, model);
    }

    // 2
    @GetMapping("/c/{category-alias}/page/{pageNum}")
    public String viewProductsByCategoryByPage(@PathVariable("category-alias") String alias,
                                               @PathVariable("pageNum") int pageNum,
                                               Model model) {
        Category category = null;
        try {
            category = categoryService.getEnabledCategoryByAlias(alias);
        } catch (CategoryNotFoundException ex) {
            log.error(ex.getMessage());
            return "error/404.html";
        }

        Page<Product> productPage = productService.getProductByCategory(category, pageNum);

        List<Category> parentCategoryList = categoryService.getAllParentsOfCategory(category);
        List<Product> productsOfCategory = productPage.getContent();
        long totalElements = productPage.getTotalElements();
        long startCount = (pageNum - 1) * ProductService.NUMBER_OF_PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.NUMBER_OF_PRODUCTS_PER_PAGE - 1;
        if(endCount > totalElements) {
            endCount = totalElements;
        }
        int totalPages = productPage.getTotalPages();

        model.addAttribute("category", category);
        model.addAttribute("categoryParents", parentCategoryList);
        model.addAttribute("categoryChildren", category.getChildren());
        model.addAttribute("products", productsOfCategory);

        model.addAttribute("totalElements", totalElements);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNum);

        return "product/products-by-category.html";
    }

    // 3
    @GetMapping("/p/{alias}")
    public String viewProductByAlias(@PathVariable("alias") String alias,
                                     Model model) {
        try {
            Product product = productService.getProductByAlias(alias);
            Category categoryOfProduct = product.getCategory();

            model.addAttribute("product", product);
            model.addAttribute("category", categoryOfProduct);
            model.addAttribute("categoryParents", categoryService.getAllParentsOfCategory(categoryOfProduct));

            return "product/product-details.html";
        } catch(ProductNotFoundException ex) {
            return "error/404.html";
        }
    }

    // 4
    @GetMapping("/search-products")
    public String searchProductsFirstPage(@RequestParam("keyword") String keyword,
                                          Model model) {
        return searchProductByPage(keyword, 1, model);
    }

    // 5
    @GetMapping("/search-products/page/{pageNum}")
    public String searchProductByPage(@RequestParam("keyword") String keyword,
                                      @PathVariable("pageNum") int pageNum,
                                      Model model) {
        Page<Product> productPage = productService.searchProducts(keyword, pageNum);

        List<Product> productList = productPage.getContent();
        long totalElements = productPage.getTotalElements();
        long startCount = (pageNum - 1) * ProductService.NUMBER_OF_PRODUCTS_SEARCH_RESULTS + 1;
        long endCount = startCount + ProductService.NUMBER_OF_PRODUCTS_SEARCH_RESULTS - 1;
        if(endCount > totalElements) {
            endCount = totalElements;
        }
        int totalPages = productPage.getTotalPages();

        model.addAttribute("products", productList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNum);

        return "product/search-results.html";
    }
}
