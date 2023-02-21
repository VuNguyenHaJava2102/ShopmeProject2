package com.shopme.checkout;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ShippingRate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CheckoutService {

    public CheckoutInfo prepareCheckout(List<CartItem> items,
                                        ShippingRate shippingRate) {
        float productCostTotal = calculateProductCostTotal(items);
        float productPriceTotal = calculateProductPriceTotal(items);
        float productShippingCostTotal = calculateProductShippingCostTotal(items, shippingRate.getRate());
        float paymentTotal = productPriceTotal + productShippingCostTotal;

        CheckoutInfo checkoutInfo = new CheckoutInfo();
        checkoutInfo.setProductCostTotal(productCostTotal);
        checkoutInfo.setProductPriceTotal(productPriceTotal);
        checkoutInfo.setProductShippingCostTotal(productShippingCostTotal);
        checkoutInfo.setPaymentTotal(paymentTotal);

        checkoutInfo.setDaysToDeliver(shippingRate.getDays());

        // Calculate delivery date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, checkoutInfo.getDaysToDeliver());
        Date deliveryDate = calendar.getTime();

        checkoutInfo.setDeliveryDate(deliveryDate);
        checkoutInfo.setCodSupported(shippingRate.isCodSupported());

        return checkoutInfo;
    }

    // 1
    private float calculateProductCostTotal(List<CartItem> items) {
        float total = 0.0f;
        for(CartItem item : items) {
            total += item.getProduct().getCost() * item.getQuantity();
        }
        return total;
    }

    // 2
    private float calculateProductPriceTotal(List<CartItem> items) {
        float total = 0.0f;
        for(CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    // 3
    private float calculateProductShippingCostTotal(List<CartItem> items,
                                                    float rate) {
        float total = 0.0f;
        for(CartItem item : items) {
            Product product = item.getProduct();

            float dim = (product.getLength() * product.getWidth() * product.getHeight()) / 139;
            float finalWeight = product.getWeight() > dim ? product.getWeight() : dim;
            float shippingCost = finalWeight * rate * item.getQuantity();

            total += shippingCost;
            item.setShippingCost(shippingCost);
        }
        return total;
    }
}
