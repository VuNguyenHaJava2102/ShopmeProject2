package com.shopme.admin.product;

import com.shopme.admin.file.FileUploadUtils;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    public static final int NUMBER_OF_PRODUCTS_PER_PAGE = 5;

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public Page<Product> getProductByPage(int pageNum,
                                          String sortField,
                                          String sortDir,
                                          String keyword,
                                          String allParentIds) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_OF_PRODUCTS_PER_PAGE, sort);
        return productRepository.searchProduct(keyword, allParentIds, pageable);
    }

    public Product saveProduct(Product product) {
        if(product.getId() == null) {
            product.setCreatedTime(new Date());
        }

        if(product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().replaceAll(" ", "_");
            product.setAlias(defaultAlias);
        } else {
            String alias = product.getAlias().replaceAll(" ", "_");
            product.setAlias(alias);
        }

        // This must be always set in both 2 cases: create and update
        product.setUpdatedTime(new Date());

        return productRepository.save(product);
    }

    public boolean checkAliasLength(String alias) {
        if(alias == null || alias.isBlank()) {
            return true;
        }
        if(alias.length() > 255) {
            return false;
        }
        return true;
    }

    public boolean checkNameDuplicated(Integer id, String name) {
        if(id == null) return productRepository.existsByName(name);

        Product product = productRepository.findByName(name);
        if(product == null) return false;

        if(product.getId() == id) return false;

        return true;
    }

    public boolean checkAliasDuplicated(Integer id, String alias, String name) {
        if(alias == null || alias.isBlank()) {
            alias = name.replaceAll(" ", "_");
        }

        if(id == null) return productRepository.existsByAlias(alias);

        Product product = productRepository.findByAlias(alias);
        if(product == null) return false;

        if(product.getId() == id) return false;

        return true;
    }

    public void updateStatus(int id) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
        Product product = optionalProduct.get();
        product.setEnabled(!product.isEnabled());
        productRepository.save(product);
    }

    public void deleteProduct(int id) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
        productRepository.delete(optionalProduct.get());

        // Delete upload directory:
        String extraImagesDir = "u/product-images/" + id + "/extras";
        String mainImageDir = "u/product-images/" + id;
        FileUploadUtils.deleteUploadDir(extraImagesDir);
        FileUploadUtils.deleteUploadDir(mainImageDir);
    }

    public Product getProductById(int id) throws ProductNotFoundException {
        try {
            return productRepository.findById(id).get();
        } catch(NoSuchElementException ex) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
    }

    // Product image
    public boolean checkDuplicatedImage(String name) {
        return productImageRepository.existsByName(name);
    }

}
