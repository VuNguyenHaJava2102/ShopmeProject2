package com.shopme.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "order_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int quantity;
    private float productCost;
    private float unitPrice;
    private float subtotal;
    private float shippingCost;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // Constructor
    public OrderDetail(String categoryName,
                       Integer id,
                       int quantity,
                       float productCost,
                       float subtotal,
                       float shippingCost) {
        this.product = new Product();
        this.product.setCategory(new Category(categoryName));
        this.id = id;
        this.quantity = quantity;
        this.productCost = productCost;
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
    }

    public OrderDetail(Integer id,
                       String productName,
                       int quantity,
                       float productCost,
                       float subtotal,
                       float shippingCost) {
        this.product = new Product();
        this.product.setName(productName);
        this.id = id;
        this.quantity = quantity;
        this.productCost = productCost;
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
    }

    @Transient
    public String getUnitShipping() {
        float unitShipping = shippingCost / quantity;
        return String.valueOf(unitShipping);
    }
}
