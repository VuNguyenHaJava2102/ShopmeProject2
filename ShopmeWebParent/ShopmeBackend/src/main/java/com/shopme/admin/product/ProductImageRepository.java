package com.shopme.admin.product;

import com.shopme.common.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    Boolean existsByName(String name);

}
