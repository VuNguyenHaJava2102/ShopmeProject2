package com.shopme.product;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductService {
    public static final int NUMBER_OF_PRODUCTS_PER_PAGE = 10;
    public static final int NUMBER_OF_PRODUCTS_SEARCH_RESULTS= 10;

    private final ProductRepository productRepository;

    // 1
    public Page<Product> getProductByCategory(Category category,
                                              int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_OF_PRODUCTS_PER_PAGE);
        String allParentIds = category.getAllParentIds();

        Page<Product> productPage = productRepository.findProductByCategory(allParentIds, pageable);

        return productPage;
    }

    // 2
    public Product getProductByAlias(String alias) throws ProductNotFoundException {
        Product product = productRepository.findByAlias(alias);
        if(product == null) {
            throw new ProductNotFoundException("Could not find any product with alias: " + alias);
        }
        return productRepository.findByAlias(alias);
    }

    // 3
    public Page<Product> searchProducts(String keyword,
                                        int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_OF_PRODUCTS_SEARCH_RESULTS);
        return productRepository.findByFullTextSearch(keyword, pageable);
    }

}
