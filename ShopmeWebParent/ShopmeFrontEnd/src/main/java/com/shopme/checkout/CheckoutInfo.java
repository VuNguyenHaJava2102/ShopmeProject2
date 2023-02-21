package com.shopme.checkout;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CheckoutInfo {

    private float productCostTotal; // total of product's original price (use for calculate revenue)
    private float productPriceTotal; // total of product's selling price (include discount percent) (2)
    private float productShippingCostTotal; // total of product's shipping cost (3)
    private float paymentTotal; // total of (2) + (3)

    private int daysToDeliver;
    private Date deliveryDate;
    private boolean codSupported;

    @Override
    public String toString() {
        return "CheckoutInfo{" +
                "productCostTotal=" + productCostTotal +
                ", productPriceTotal=" + productPriceTotal +
                ", productShippingCostTotal=" + productShippingCostTotal +
                ", paymentTotal=" + paymentTotal +
                ", daysToDeliver=" + daysToDeliver +
                ", deliveryDate=" + deliveryDate +
                ", codSupported=" + codSupported +
                '}';
    }
}
