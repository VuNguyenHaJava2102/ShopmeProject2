package com.shopme.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Product's name must not be blank")
    @Size(max = 255, message = "Maximum length of product's name is 255 character!")
    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String alias;

    @NotBlank(message = "Short description must not be blank")
    @Size(max = 1024, message = "Maximum length of product's name is 1024 character!")
    @Column(name = "short_description", nullable = false, length = 1024)
    private String shortDescription;

    @NotBlank(message = "Full description must not be blank")
    @Size(max = 4096, message = "Maximum length of product's name is 4096 character!")
    @Column(name = "full_description", nullable = false, length = 4096)
    private String fullDescription;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    private boolean enabled;

    @Column(name = "in_stock")
    private boolean inStock;

    @Positive(message = "Cost must be greater than 0!")
    private float cost;
    @Positive(message = "Price must be greater than 0!")
    private float price;
    @PositiveOrZero(message = "Discount percent must be greater or equal to 0!")
    @Column(name = "discount_percent")
    private float discountPercent;

    @Positive(message = "Length must be greater than 0!")
    private float length;
    @Positive(message = "Width must be greater than 0!")
    private float width;
    @Positive(message = "Height must be greater than 0!")
    private float height;
    @Positive(message = "Weight must be greater than 0")
    private float weight;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductDetail> details = new HashSet<>();

    public void addExtraImage(String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }

    public void addDetail(String name, String value) {
        this.details.add(new ProductDetail(name, value, this));
    }

    @Transient
    public String getMainImagePath() {
        if(this.id == null)
            return "/images/image-thumbnail.png";
        return "/u/product-images/" + this.id + "/" + this.mainImage;
    }

    @Transient
    public String getShorterName() {
        return this.name.length() > 50 ? this.name.substring(0, 50).concat("...") : this.name;
    }

    @Transient
    public float getDiscountPrice() {
        return (price * (100 - discountPercent)) / 100;
    }

}
