package com.shopme.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.annotation.Order;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Category name must not be blank")
    @Size(min = 2, max = 128, message = "Category name must have between 128 characters")
    @Column(unique = true, nullable = false, length = 128)
    private String name;

    @NotBlank(message = "Category alias must not be blank")
    @Size(min = 2, max = 64, message = "Category's alias must have between 2-64 characters")
    @Column(unique = true, nullable = false, length = 64)
    private String alias;

    @Column(nullable = false, length = 128)
    private String image;
    private boolean enabled;

    @Column(name = "all_parent_id")
    private String allParentIds;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("name")
    private Set<Category> children = new HashSet<>();

    // Constructor
    public Category(String name) {
        this.name = name;
        this.alias = name;
        this.image = "default-image.png";
    }

    public Category(String name, Category parent) {
        this(name);
        this.parent = parent;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Transient
    @Transient
    public String getImagePath() {
        if(this.id == null)
            return "/images/image-thumbnail.png";
        return "/u/category-images/" + this.id + "/" + this.image;
    }

    @Transient
    private boolean isHasChildren;

    public static Category copyIdAndName(Category category) {
        Category copyCategory = new Category();

        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setAllParentIds(category.getAllParentIds());

        return copyCategory;
    }

    // Utility method
    public static Category copyAll(Category category) {
        Category copyCategory = new Category();

        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setEnabled(category.isEnabled());
        copyCategory.setImage(category.getImage());
        copyCategory.setParent(category.getParent());
        copyCategory.setAllParentIds(category.getAllParentIds());
        copyCategory.setChildren(category.getChildren());
        copyCategory.setHasChildren(category.getChildren().size() > 0);

        return copyCategory;
    }

}
