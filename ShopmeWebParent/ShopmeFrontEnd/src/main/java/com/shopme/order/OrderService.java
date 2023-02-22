package com.shopme.order;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.Product;
import com.shopme.common.enums.OrderStatus;
import com.shopme.common.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order saveOrder(CheckoutInfo checkoutInfo,
                           Customer customer,
                           Address address,
                           List<CartItem> cartItemList,
                           PaymentMethod paymentMethod) {
        Order newOrder = new Order();

        if(address == null) {
            newOrder.copyAddressFromCustomer(customer);
        } else {
            newOrder.copyAddressFromAddress(address);
        }

        newOrder.setOrderTime(new Date());
        newOrder.setDaysToDeliver(checkoutInfo.getDaysToDeliver());
        newOrder.setDeliveryDay(checkoutInfo.getDeliveryDate());

        newOrder.setProductCost(checkoutInfo.getProductCostTotal());
        newOrder.setShippingCost(checkoutInfo.getProductShippingCostTotal());
        newOrder.setSubtotal(checkoutInfo.getProductPriceTotal());
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setTax(0.0f);

        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setCustomer(customer);

        // Convert cart item to order detail
        Set<OrderDetail> orderDetailSet = new HashSet<>();
        for(CartItem item : cartItemList) {
            Product product = item.getProduct();
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setProductCost(product.getCost());
            orderDetail.setUnitPrice(product.getDiscountPrice());
            orderDetail.setSubtotal(item.getSubtotal());
            orderDetail.setShippingCost(item.getShippingCost());
            orderDetail.setProduct(product);
            orderDetail.setOrder(newOrder);

            orderDetailSet.add(orderDetail);
        }
        newOrder.setOrderDetails(orderDetailSet);
        return orderRepository.save(newOrder);
    }
}
