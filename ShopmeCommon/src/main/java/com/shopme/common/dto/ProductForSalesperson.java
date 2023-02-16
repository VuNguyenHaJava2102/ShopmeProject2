package com.shopme.common.dto;

import javax.persistence.Column;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public class ProductForSalesperson {

    @Positive(message = "Cost must be greater than 0!")
    private float cost;
    @Positive(message = "Price must be greater than 0!")
    private float price;
    @PositiveOrZero(message = "Discount percent must be greater or equal to 0!")
    @Column(name = "discount_percent")
    private float discountPercent;

}
